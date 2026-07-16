package net.changed.ability.tree;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.changed.entity.variant.TransfurVariant;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Instantiated per player, exists outside TransfurVariantInstance to persist between variants
 */
public class AbilityTreeInstance {
    public static final int POINTS_PER_LEVEL = 10;

    public record AccountedPurchase(ResourceLocation nodeName, TransfurVariant<?> variant, int price) {
        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (obj instanceof AccountedPurchase other)
                return other.nodeName.equals(nodeName) && other.variant.equals(variant) && other.price == price;
            return false;
        }
    }

    public record NodeState(ResourceLocation nodeName, AbilityTree.Node node, boolean unlocked) {}

    public static class AccountedTree {
        private final AbilityTree tree;
        private final Set<AccountedPurchase> purchasedNodes = new HashSet<>();

        private final Map<TransfurVariant<?>, Integer> pointStores = new HashMap<>();

        public AccountedTree(AbilityTree tree) {
            this.tree = tree;
        }

        public AccountedTree(AbilityTree tree, Set<AccountedPurchase> purchasedNodes, Map<TransfurVariant<?>, Integer> pointStores) {
            this.tree = tree;
            this.purchasedNodes.addAll(purchasedNodes);
            this.pointStores.putAll(pointStores);
        }

        public boolean makePurchase(ResourceLocation nodeName, TransfurVariant<?> variant, int price) {
            if (tree.getNode(nodeName) == null)
                return false;

            if (purchasedNodes.stream().anyMatch(purchase -> {
                return purchase.nodeName.equals(nodeName) && purchase.variant == variant;
            })) return false;

            purchasedNodes.add(new AccountedPurchase(nodeName, variant, price));
            return true;
        }

        public int refundNodePurchases(ResourceLocation nodeName) {
            if (tree.getNode(nodeName) == null)
                return 0;

            if (purchasedNodes.stream().noneMatch(purchase -> {
                return purchase.nodeName.equals(nodeName);
            })) return 0;

            var toRemove = purchasedNodes.stream().filter(purchase -> {
                return purchase.nodeName.equals(nodeName);
            }).toList();

            toRemove.forEach(purchase -> {
                purchasedNodes.remove(purchase);
                pointStores.compute(purchase.variant, (variant, points) -> {
                    if (points == null)
                        return purchase.price;
                    else
                        return points + purchase.price;
                });
            });

            return toRemove.size();
        }

        public AbilityTree getTree() {
            return tree;
        }

        public Stream<AccountedPurchase> getPurchasesFor(ResourceLocation nodeName) {
            return purchasedNodes.stream().filter(purchase -> purchase.nodeName.equals(nodeName));
        }

        public boolean hasPrerequisites(ResourceLocation nodeName, TransfurVariant<?> forVariant) {
            final var node = tree.getNode(nodeName);
            if (node == null)
                return false;
            // TODO maybe let node specify criteria as well
            if (node.parentNode.equals(AbilityTree.ROOT_NAME))
                return true;

            return getNodeStates(forVariant, pair -> {
                return pair.getFirst().equals(node.parentNode);
            }).allMatch(NodeState::unlocked);
        }

        public Stream<NodeState> getNodeStates(TransfurVariant<?> forVariant) {
            return getNodeStates(forVariant, pair -> true);
        }

        public Stream<NodeState> getNodeStates(TransfurVariant<?> forVariant, Predicate<Pair<ResourceLocation, AbilityTree.Node>> nodePredicate) {
            return tree.getNodes().filter(nodePredicate)
                    .map(node -> {
                        boolean unlocked = getPurchasesFor(node.getFirst()).anyMatch(purchase -> {
                            if (purchase.variant == forVariant)
                                return true; // This variant unlocked the node
                            if (node.getSecond().price + node.getSecond().groupDiscount <= 0)
                                return true; // Another variant unlocked the node, and the discount makes it free
                            return false;
                        });
                        return new NodeState(node.getFirst(), node.getSecond(), unlocked);
                    });
        }

        public int getEffectivePrice(ResourceLocation nodeName, TransfurVariant<?> forVariant) {
            return tree.getNodeSafe(nodeName).map(node -> {
                boolean applyDiscount = getPurchasesFor(nodeName).anyMatch(purchase -> purchase.variant != forVariant && purchase.price >= node.price);
                return applyDiscount ? node.price + node.groupDiscount : node.price;
            }).orElseThrow(() -> new IllegalArgumentException("Unknown node by name " + nodeName));
        }

        public void refundInvalidNodes() {
            Set<AccountedPurchase> invalid = new HashSet<>();
            purchasedNodes.forEach(purchase -> {
                if (tree.getNode(purchase.nodeName) == null)
                    invalid.add(purchase);
            });
            invalid.forEach(purchase -> {
                purchasedNodes.remove(purchase);
                pointStores.compute(purchase.variant, (variant, points) -> {
                    if (points == null)
                        return purchase.price;
                    else
                        return points + purchase.price;
                });
            });
        }

        public void applyEffects(AbilityCounter counter) {
            Set<ResourceLocation> occludedNodes = new HashSet<>();
            getNodeStates(counter.variantInstance.getParent()).filter(NodeState::unlocked)
                    .forEach(nodeState -> occludedNodes.addAll(nodeState.node.occludes));

            getNodeStates(counter.variantInstance.getParent()).forEach(nodeState -> {
                if (nodeState.unlocked) {
                    if (!occludedNodes.contains(nodeState.nodeName)) {
                        nodeState.node.acquiredEffects.forEach(nodeEffect -> {
                            nodeEffect.applyEffect(counter);
                        });
                    }
                } else {
                    nodeState.node.missingEffects.forEach(nodeEffect -> {
                        nodeEffect.applyEffect(counter);
                    });
                }
            });
        }

        public boolean appliesTo(TransfurVariant<?> variant) {
            return tree.appliesTo(variant);
        }
    }

    private final Set<AccountedTree> trees = new HashSet<>();

    public Set<AccountedTree> getTrees() {
        return ImmutableSet.copyOf(trees);
    }

    public Set<AccountedTree> getTrees(TransfurVariant<?> forVariant) {
        return trees.stream().filter(tree -> tree.appliesTo(forVariant)).collect(Collectors.toSet());
    }

    public void updateTrees() {
        var treeDefinitions = AbilityTrees.INSTANCE.getTrees();
        if (trees.size() == treeDefinitions.size() && trees.stream().allMatch(accountedTree ->
            treeDefinitions.stream().anyMatch(accountedTree.tree::matchLocation)
        )) {
            return;
        }

        Set<AccountedTree> newAccountedTrees = new HashSet<>();
        trees.forEach(accountedTree -> {
            var newTree = treeDefinitions.stream().filter(accountedTree.tree::matchLocation).findFirst();
            if (newTree.isEmpty())
                return;
            var newAccountedTree = new AccountedTree(
                    newTree.get(),
                    accountedTree.purchasedNodes,
                    accountedTree.pointStores
            );
            newAccountedTree.refundInvalidNodes();
            newAccountedTrees.add(newAccountedTree);
        });

        treeDefinitions.forEach(abilityTree -> {
            if (newAccountedTrees.stream().anyMatch(accountedTree -> accountedTree.tree == abilityTree))
                return;

            newAccountedTrees.add(new AccountedTree(abilityTree));
        });

        trees.clear();
        trees.addAll(newAccountedTrees);
    }

    public void applyEffects(AbilityCounter counter) {
        trees.forEach(tree -> {
            if (tree.appliesTo(counter.variantInstance.getParent()))
                tree.applyEffects(counter);
        });
    }
}
