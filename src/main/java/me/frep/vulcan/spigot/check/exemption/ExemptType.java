package me.frep.vulcan.spigot.check.exemption;

import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import java.util.function.Function;
import me.frep.vulcan.spigot.check.configuration.CheckUtils;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.lifecycle.PluginServices;
import me.frep.vulcan.spigot.player.click.ClickTimingData;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.player.settings.PlayerToggles;
import me.frep.vulcan.spigot.task.TickProcessor;
import me.frep.vulcan.spigot.tracker.connection.ConnectionTracker;
import me.frep.vulcan.spigot.tracker.movement.RelativeMoveTracker;
import me.frep.vulcan.spigot.tracker.velocity.VelocityTracker;
import me.frep.vulcan.spigot.world.block.BlockUtils;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.util.NumberConversions;

public enum ExemptType {

    TPS(ExemptType::matchesTps),
    CHUNK(ExemptType::matchesChunk),
    JOINED(ExemptType::matchesJoined),
    CAKE(ExemptType::matchesCake),
    BAMBOO(ExemptType::matchesBamboo),
    HAPPY_GHAST(ExemptType::matchesHappyGhast),
    PACKET_EXPLOSION(ExemptType::matchesPacketExplosion),
    ENTITY_CRAM_FIX(ExemptType::matchesEntityCramFix),
    AROUND_SLIME(ExemptType::matchesAroundSlime),
    SIGN(ExemptType::matchesSign),
    PRESSURE_PLATE(ExemptType::matchesPressurePlate),
    SWIMMING(ExemptType::matchesSwimming),
    SWIMMING_JESUS(ExemptType::matchesSwimmingJesus),
    COLLIDING_HORIZONTALLY(ExemptType::matchesCollidingHorizontally),
    DRIPSTONE(ExemptType::matchesDripstone),
    HIGH_FLY_SPEED(ExemptType::matchesHighFlySpeed),
    GLITCHED_BLOCKS_ABOVE(ExemptType::matchesGlitchedBlocksAbove),
    PROJECTILE_DAMAGE(ExemptType::matchesProjectileDamage),
    CHEST(ExemptType::matchesChest),
    ATTACK_DAMAGE(ExemptType::matchesAttackDamage),
    SKULL(ExemptType::matchesSkull),
    EXPLOSION(ExemptType::matchesExplosion),
    STAIRS(ExemptType::matchesStairs),
    HIGH_LEVITATION(ExemptType::matchesHighLevitation),
    FENCE_GATE(ExemptType::matchesFenceGate),
    POWDER_SNOW(ExemptType::matchesPowderSnow),
    DRAGON_DAMAGE(ExemptType::matchesDragonDamage),
    JUMP_BOOST_RAN_OUT(ExemptType::matchesJumpBoostRanOut),
    LAGGED_NEAR_GROUND(ExemptType::matchesLaggedNearGround),
    SNOW(ExemptType::matchesSnow),
    NETHERITE_ARMOR(ExemptType::matchesNetheriteArmor),
    FAST_ZERO(ExemptType::matchesFastZero),
    JOINED_CHUNK_LOAD(ExemptType::matchesJoinedChunkLoad),
    NEAR_SOLID(ExemptType::matchesNearSolid),
    CAMPFIRE(ExemptType::matchesCampfire),
    CANCELLED_MOVE(ExemptType::matchesCancelledMove),
    CARPET(ExemptType::matchesCarpet),
    FLOWER_POT(ExemptType::matchesFlowerPot),
    EMPTIED_BUCKET(ExemptType::matchesEmptiedBucket),
    END_ROD(ExemptType::matchesEndRod),
    HOPPER(ExemptType::matchesHopper),
    CHAIN(ExemptType::matchesChain),
    DOOR(ExemptType::matchesDoor),
    PICKED_UP_ITEM(ExemptType::matchesPickedUpItem),
    ANVIL(ExemptType::matchesAnvil),
    LAGGED_NEAR_GROUND_MODERN(ExemptType::matchesLaggedNearGroundModern),
    FULLY_SUBMERGED(ExemptType::matchesFullySubmerged),
    RESPAWN(ExemptType::matchesRespawn),
    ICE(ExemptType::matchesIce),
    BED(ExemptType::matchesBed),
    SLAB(ExemptType::matchesSlab),
    HIGH_JUMP_BOOST(ExemptType::matchesHighJumpBoost),
    LILY_PAD(ExemptType::matchesLilyPad),
    HIGH_SPEED(ExemptType::matchesHighSpeed),
    WALL(ExemptType::matchesWall),
    SWEET_BERRIES(ExemptType::matchesSweetBerries),
    CONDUIT(ExemptType::matchesConduit),
    PLACED_WEB(ExemptType::matchesPlacedWeb),
    PISTON(ExemptType::matchesPiston),
    CHORUS_FRUIT(ExemptType::matchesChorusFruit),
    ENDER_PEARL(ExemptType::matchesEnderPearl),
    NEAR_GROUND(ExemptType::matchesNearGround),
    SLEEPING(ExemptType::matchesSleeping),
    SOUL_SPEED(ExemptType::matchesSoulSpeed),
    TRAPDOOR(ExemptType::matchesTrapdoor),
    FISHING_ROD(ExemptType::matchesFishingRod),
    PLUGIN_LOAD(ExemptType::matchesPluginLoad),
    RIPTIDE(ExemptType::matchesRiptide),
    FIREWORK(ExemptType::matchesFirework),
    SPECTATOR(ExemptType::matchesSpectator),
    CAULDRON(ExemptType::matchesCauldron),
    ENTITY_COLLISION(ExemptType::matchesEntityCollision),
    DEATH(ExemptType::matchesDeath),
    DEPTH_STRIDER(ExemptType::matchesDepthStrider),
    LEVITATION(ExemptType::matchesLevitation),
    FROZEN(ExemptType::matchesFrozen),
    DIGGING(ExemptType::matchesDigging),
    BLOCK_BREAK(ExemptType::matchesBlockBreak),
    PLACING(ExemptType::matchesPlacing),
    SERVER_POSITION(ExemptType::matchesServerPosition),
    SERVER_POSITION_FAST(ExemptType::matchesServerPositionFast),
    CANCELLED_PLACE(ExemptType::matchesCancelledPlace),
    BLOCK_PLACE(ExemptType::matchesBlockPlace),
    BLOCK_PLACE_FAST(ExemptType::matchesBlockPlaceFast),
    SWIMMING_ON_OLD_VERSION(ExemptType::matchesSwimmingOnOldVersion),
    FULLY_STUCK(ExemptType::matchesFullyStuck),
    PLACED_CLIMBABLE(ExemptType::matchesPlacedClimbable),
    PARTIALLY_STUCK(ExemptType::matchesPartiallyStuck),
    NOT_MOVING(ExemptType::matchesNotMoving),
    PLACED_SLIME(ExemptType::matchesPlacedSlime),
    FIREBALL(ExemptType::matchesFireball),
    WORLD_CHANGE(ExemptType::matchesWorldChange),
    FALL_DAMAGE(ExemptType::matchesFallDamage),
    ILLEGAL_BLOCK(ExemptType::matchesIllegalBlock),
    HONEY(ExemptType::matchesHoney),
    SCAFFOLDING(ExemptType::matchesScaffolding),
    HALF_BLOCK(ExemptType::matchesHalfBlock),
    SHULKER(ExemptType::matchesShulker),
    GLIDING(ExemptType::matchesGliding),
    ELYTRA(ExemptType::matchesElytra),
    VELOCITY(ExemptType::matchesVelocity),
    DEAD(ExemptType::matchesDead),
    WEB(ExemptType::matchesWeb),
    SOUL_SAND(ExemptType::matchesSoulSand),
    CINEMATIC(ExemptType::matchesCinematic),
    FAST(ExemptType::matchesFast),
    LENIENT_SCAFFOLDING(ExemptType::matchesLenientScaffolding),
    WINDOW_CLICK(ExemptType::matchesWindowClick),
    DROPPED_ITEM(ExemptType::matchesDroppedItem),
    CANCELLED_BREAK(ExemptType::matchesCancelledBreak),
    MYTHIC_MOB(ExemptType::matchesMythicMob),
    CREATIVE(ExemptType::matchesCreative),
    AUTOCLICKER_NON_DIG(ExemptType::matchesAutoclickerNonDig),
    AUTOCLICKER(ExemptType::matchesAutoclicker),
    COLLIDING_VERTICALLY(ExemptType::matchesCollidingVertically),
    SLOW_FALLING(ExemptType::matchesSlowFalling),
    BUBBLE_COLUMN(ExemptType::matchesBubbleColumn),
    FENCE(ExemptType::matchesFence),
    JUMP_BOOST(ExemptType::matchesJumpBoost),
    FLIGHT(ExemptType::matchesFlight),
    COMBO_MODE(ExemptType::matchesComboMode),
    DOLPHINS_GRACE(ExemptType::matchesDolphinsGrace),
    TELEPORT(ExemptType::matchesTeleport),
    SERVER_VERSION(ExemptType::matchesServerVersion),
    KELP(ExemptType::matchesKelp),
    CLIENT_VERSION(ExemptType::matchesClientVersion),
    SLIME(ExemptType::matchesSlime),
    BOAT(ExemptType::matchesBoat),
    SEA_PICKLE(ExemptType::matchesSeaPickle),
    SEAGRASS(ExemptType::matchesSeagrass),
    TURTLE_EGG(ExemptType::matchesTurtleEgg),
    SHULKER_BOX(ExemptType::matchesShulkerBox),
    WATERLOGGED(ExemptType::matchesWaterlogged),
    CLIMBABLE(ExemptType::matchesClimbable),
    LIQUID(ExemptType::matchesLiquid),
    GLASS_PANE(ExemptType::matchesGlassPane),
    FARMLAND(ExemptType::matchesFarmland),
    VEHICLE(ExemptType::matchesVehicle),
    VOID(ExemptType::matchesVoid);

    private final Function<PlayerData, Boolean> function;

    private ExemptType(final Function<PlayerData, Boolean> nullxx) {
        this.function = nullxx;
    }

    public Function<PlayerData, Boolean> getFunction() {
        return this.function;
    }

    private static Boolean matchesVoid(PlayerData playerData) {
        boolean belowYLimit;
        if (ExemptionManager.isAtLeast1_18()) {
            CollisionState collisionState = playerData.getCollisionState();
            belowYLimit = collisionState.getY() < -70.0;
        } else {
            CollisionState collisionState2 = playerData.getCollisionState();
            belowYLimit = collisionState2.getY() < 0.0;
        }
        return belowYLimit;
    }

    private static Boolean matchesVehicle(PlayerData playerData) {
        CollisionState collisionState = playerData.getCollisionState();
        if (!(collisionState.getTicksSinceVehicle() >= 25)) {
            return true;
        }
        if ((playerData.getPlayer() != null) && (playerData.getPlayer().getVehicle() != null)) {
            return true;
        }
        return false;
    }

    private static Boolean matchesFarmland(PlayerData playerData) {
        CollisionState collisionState = playerData.getCollisionState();
        return collisionState.getTicksSinceFarmland() < 20;
    }

    private static Boolean matchesGlassPane(PlayerData playerData) {
        CollisionState collisionState = playerData.getCollisionState();
        return collisionState.isGlassPane();
    }

    private static Boolean matchesLiquid(PlayerData playerData) {
        CollisionState collisionState = playerData.getCollisionState();
        return collisionState.getTicksSinceLiquid() < 25;
    }

    private static Boolean matchesClimbable(PlayerData playerData) {
        if ((!playerData.getCollisionState().isLadderOrVine()) && (!playerData.getCollisionState().isClimbable())) {
            return false;
        }
        return true;
    }

    private static Boolean matchesWaterlogged(PlayerData playerData) {
        if ((ExemptionManager.isNewerThan1_13()) && (playerData.getCollisionState().getTicksSinceWaterlogged() < 30)) {
            return true;
        }
        return false;
    }

    private static Boolean matchesShulkerBox(PlayerData playerData) {
        if ((!playerData.getCollisionState().isShulkerBox()) && (playerData.getCollisionState().getTicksSinceShulkerBox() >= 60)) {
            return false;
        }
        return true;
    }

    private static Boolean matchesTurtleEgg(PlayerData playerData) {
        return playerData.getCollisionState().isTurtleEgg();
    }

    private static Boolean matchesSeagrass(PlayerData playerData) {
        return playerData.getCollisionState().isSeagrass();
    }

    private static Boolean matchesSeaPickle(PlayerData playerData) {
        return playerData.getCollisionState().isSeaPickle();
    }

    private static Boolean matchesBoat(PlayerData playerData) {
        return playerData.getCollisionState().isBoat();
    }

    private static Boolean matchesSlime(PlayerData playerData) {
        if (!playerData.getCollisionState().isSlime()) {
            CollisionState collisionState = playerData.getCollisionState();
            if (collisionState.getTicksSinceSlime() >= 65) {
                return false;
            }
        }
        return true;
    }

    private static Boolean matchesClientVersion(PlayerData playerData) {
        return playerData.getUser().getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9);
    }

    private static Boolean matchesKelp(PlayerData playerData) {
        return playerData.getCollisionState().isKelp();
    }

    private static Boolean matchesServerVersion(PlayerData playerData) {
        return ExemptionManager.isAtLeast1_9();
    }

    private static Boolean matchesTeleport(PlayerData playerData) {
        if ((playerData.getMotionState().getTicksSinceTeleport() >= 10) && (!playerData.getMotionState().isTeleporting())) {
            return false;
        }
        return true;
    }

    private static Boolean matchesDolphinsGrace(PlayerData playerData) {
        if (ExemptionManager.isAtLeast1_9()) {
            CollisionState collisionState = playerData.getCollisionState();
            if (collisionState.getTicksSinceDolphinsGrace() < 50) {
                return true;
            }
            if (CheckUtils.hasDolphinsGrace(playerData)) {
                return true;
            }
        }
        return false;
    }

    private static Boolean matchesComboMode(PlayerData playerData) {
        if ((playerData.getMotionState().getTicksSinceAttackDamage() < 25) && (playerData.getPlayer() != null) && (playerData.getPlayer().getMaximumNoDamageTicks() < 15)) {
            return true;
        }
        return false;
    }

    private static Boolean matchesFlight(PlayerData playerData) {
        if ((playerData.getPlayer() != null) && (playerData.getPlayer().getAllowFlight())) {
            return true;
        }
        CollisionState collisionState = playerData.getCollisionState();
        return collisionState.getTicksSinceFlightAllowed() >= VulcanSettings.settingsFlightCooldown ? false : true;
    }

    private static Boolean matchesJumpBoost(PlayerData playerData) {
        if (playerData.getCollisionState().getTicksSinceJumpBoost() >= 40) {
            MotionState motionState = playerData.getMotionState();
            if (!motionState.isJumpBoostEffect()) {
                return false;
            }
        }
        return true;
    }

    private static Boolean matchesFence(PlayerData playerData) {
        if (!playerData.getCollisionState().isFence()) {
            CollisionState collisionState = playerData.getCollisionState();
            if (collisionState.getTicksSinceFence() >= 7) {
                return false;
            }
        }
        return true;
    }

    private static Boolean matchesBubbleColumn(PlayerData playerData) {
        if ((ExemptionManager.isAtLeast1_9()) && (playerData.getCollisionState().getTicksSinceBubbleColumn() < 40)) {
            return true;
        }
        return false;
    }

    private static Boolean matchesSlowFalling(PlayerData playerData) {
        if ((ExemptionManager.isAtLeast1_9()) && (playerData.getCollisionState().getTicksSinceSlowFalling() < 40)) {
            return true;
        }
        return false;
    }

    private static Boolean matchesCollidingVertically(PlayerData playerData) {
        CollisionState collisionState = playerData.getCollisionState();
        return collisionState.getTicksSinceVerticalCollision() < 20;
    }

    private static Boolean matchesAutoclicker(PlayerData playerData) {
        PlayerToggles playerToggles = playerData.getPlayerToggles();
        if (!playerToggles.isAnyExempt(new ExemptType[] { PLACING, DIGGING, BLOCK_BREAK, FAST, DROPPED_ITEM, WINDOW_CLICK, CREATIVE, CANCELLED_BREAK })) {
            long currentTimeMillis2 = System.currentTimeMillis();
            MotionState motionState = playerData.getMotionState();
            if (currentTimeMillis2 - motionState.getDigStartedAtMillis() >= 110L) {
                CollisionState collisionState = playerData.getCollisionState();
                if (collisionState.getTicksSinceFirework() >= 20) {
                    long currentTimeMillis3 = System.currentTimeMillis();
                    if (currentTimeMillis3 - playerData.getClickTimingData().getLastPlaceAt() >= 3000L) {
                        long currentTimeMillis4 = System.currentTimeMillis();
                        ClickTimingData clickTimingData = playerData.getClickTimingData();
                        if ((currentTimeMillis4 - clickTimingData.getLastDigAt() >= 250L) && (playerData.getMotionState().getTicksSinceBottleUse() >= 15)) {
                            long currentTimeMillis5 = System.currentTimeMillis();
                            motionState = playerData.getMotionState();
                            if (currentTimeMillis5 - motionState.getDigCancelledAtMillis() >= 110L) {
                                long currentTimeMillis6 = System.currentTimeMillis();
                                motionState = playerData.getMotionState();
                                if (currentTimeMillis6 - motionState.getDigFinishedAtMillis() >= 110L) {
                                    TickProcessor tickProcessor = PluginServices.INSTANCE.getTickProcessor();
                                    int totalTicks = tickProcessor.getTotalTicks();
                                    motionState = playerData.getMotionState();
                                    if (totalTicks >= 20) {
                                        TickProcessor tickProcessor2 = PluginServices.INSTANCE.getTickProcessor();
                                        int totalTicks2 = tickProcessor2.getTotalTicks();
                                        if (totalTicks2 - playerData.getMotionState().getLastProjectileLaunchTick() >= 20) {
                                            TickProcessor tickProcessor3 = PluginServices.INSTANCE.getTickProcessor();
                                            int totalTicks3 = tickProcessor3.getTotalTicks();
                                            RelativeMoveTracker relativeMoveTracker = playerData.getRelativeMoveTracker();
                                            if (totalTicks3 - relativeMoveTracker.getLastItemFrameInteractTick() >= 20) {
                                                if (playerData.getMotionState().getDistanceFromDiggedBlock() < 6.5) {
                                                    if (playerData.getMotionState().getDistanceFromDiggedBlock() > 0.0) {
                                                        return true;
                                                    }
                                                }
                                                return false;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private static Boolean matchesAutoclickerNonDig(PlayerData playerData) {
        PlayerToggles playerToggles = playerData.getPlayerToggles();
        if ((!playerToggles.isAnyExempt(new ExemptType[] { PLACING, DIGGING, BLOCK_BREAK, FAST, DROPPED_ITEM, WINDOW_CLICK, CANCELLED_BREAK, CREATIVE })) && (playerData.getMotionState().getTicksSinceBottleUse() >= 15)) {
            TickProcessor tickProcessor = PluginServices.INSTANCE.getTickProcessor();
            int totalTicks = tickProcessor.getTotalTicks();
            RelativeMoveTracker relativeMoveTracker = playerData.getRelativeMoveTracker();
            if (totalTicks - relativeMoveTracker.getLastItemFrameInteractTick() >= 20) {
                TickProcessor tickProcessor2 = PluginServices.INSTANCE.getTickProcessor();
                int totalTicks2 = tickProcessor2.getTotalTicks();
                if (totalTicks2 - playerData.getMotionState().getLastProjectileLaunchTick() >= 20) {
                    return false;
                }
            }
        }
        return true;
    }

    private static Boolean matchesCreative(PlayerData playerData) {
        if ((playerData.getPlayer() != null) && (playerData.getPlayer().getGameMode() == GameMode.CREATIVE)) {
            return true;
        }
        return playerData.getMotionState().getGameMode() != GameMode.CREATIVE ? false : true;
    }

    private static Boolean matchesMythicMob(PlayerData playerData) {
        return playerData.getMotionState().getTicksSinceMythicMob() < 50;
    }

    private static Boolean matchesCancelledBreak(PlayerData playerData) {
        TickProcessor tickProcessor = PluginServices.INSTANCE.getTickProcessor();
        int totalTicks = tickProcessor.getTotalTicks();
        return totalTicks - playerData.getMotionState().getLastCancelledBreakTick() < 40;
    }

    private static Boolean matchesDroppedItem(PlayerData playerData) {
        TickProcessor tickProcessor = PluginServices.INSTANCE.getTickProcessor();
        int totalTicks = tickProcessor.getTotalTicks();
        return totalTicks - playerData.getMotionState().getLastDroppedItemTick() < 15;
    }

    private static Boolean matchesWindowClick(PlayerData playerData) {
        TickProcessor tickProcessor = PluginServices.INSTANCE.getTickProcessor();
        int totalTicks = tickProcessor.getTotalTicks();
        return totalTicks - playerData.getMotionState().getLastWindowClickTick() < 15;
    }

    private static Boolean matchesLenientScaffolding(PlayerData playerData) {
        if ((VulcanSettings.settingsLenientScaffolding) && (ExemptionManager.isAtLeast1_9()) && (playerData.getMotionState().getDistanceFromPlacedBlock() < 3.0) && (playerData.getMotionState().getDistanceFromPlacedBlock() > 0.0)) {
            return true;
        }
        return false;
    }

    private static Boolean matchesFast(PlayerData playerData) {
        return playerData.getConnectionTracker().isFastMovement();
    }

    private static Boolean matchesCinematic(PlayerData playerData) {
        if ((VulcanSettings.settingsCinematic) && (playerData.getRotationTracker().isCinematic())) {
            return true;
        }
        return false;
    }

    private static Boolean matchesSoulSand(PlayerData playerData) {
        return playerData.getCollisionState().isSoulSand();
    }

    private static Boolean matchesWeb(PlayerData playerData) {
        if (playerData.getCollisionState().getTicksSinceWeb() < 25) {
            CollisionState collisionState = playerData.getCollisionState();
            if (collisionState.getDeltaY() < 0.1) {
                return true;
            }
        }
        return false;
    }

    private static Boolean matchesDead(PlayerData playerData) {
        if ((playerData.getPlayer() != null) && (playerData.getPlayer().isDead())) {
            return true;
        }
        return false;
    }

    private static Boolean matchesVelocity(PlayerData playerData) {
        VelocityTracker velocityTracker = playerData.getVelocityTracker();
        return velocityTracker.getTicksSinceVelocity() < 40;
    }

    private static Boolean matchesElytra(PlayerData playerData) {
        MotionState motionState = playerData.getMotionState();
        if (!motionState.isWearingElytra()) {
            CollisionState collisionState = playerData.getCollisionState();
            if (collisionState.getTicksSinceWearingElytra() >= 100) {
                return false;
            }
        }
        return true;
    }

    private static Boolean matchesGliding(PlayerData playerData) {
        if (ExemptionManager.isAtLeast1_9()) {
            MotionState motionState = playerData.getMotionState();
            if (motionState.getTicksSinceGliding() < 10) {
                return true;
            }
        }
        return false;
    }

    private static Boolean matchesShulker(PlayerData playerData) {
        if ((!playerData.getCollisionState().isShulker()) && (playerData.getCollisionState().getTicksSinceShulker() >= 40)) {
            return false;
        }
        return true;
    }

    private static Boolean matchesHalfBlock(PlayerData playerData) {
        CollisionState collisionState = playerData.getCollisionState();
        if (!collisionState.isStairs()) {
            collisionState = playerData.getCollisionState();
            if (!collisionState.isSlab()) {
                return false;
            }
        }
        return true;
    }

    private static Boolean matchesScaffolding(PlayerData playerData) {
        if (ExemptionManager.isAtLeast1_9()) {
            if (playerData.getCollisionState().isScaffolding()) {
                return true;
            }
            if (playerData.getCollisionState().getTicksSinceScaffolding() < 30) {
                return true;
            }
        }
        return false;
    }

    private static Boolean matchesHoney(PlayerData playerData) {
        if (ExemptionManager.isAtLeast1_9()) {
            if (playerData.getCollisionState().isHoney()) {
                return true;
            }
            if (playerData.getCollisionState().getTicksSinceHoney() < 30) {
                return true;
            }
        }
        return false;
    }

    private static Boolean matchesIllegalBlock(PlayerData playerData) {
        CollisionState collisionState = playerData.getCollisionState();
        if ((!collisionState.isDaylightDetector()) && (!playerData.getCollisionState().isBrewingStand()) && (!playerData.getCollisionState().isCarpet()) && (!playerData.getCollisionState().isWall()) && (!playerData.getCollisionState().isSkull()) && (!playerData.getCollisionState().isEndPortalFrame()) && (!playerData.getCollisionState().isScaffolding())) {
            collisionState = playerData.getCollisionState();
            if (!collisionState.isCampfire()) {
                return false;
            }
        }
        return true;
    }

    private static Boolean matchesFallDamage(PlayerData playerData) {
        return playerData.getMotionState().getTicksSinceFallDamage() < 40;
    }

    private static Boolean matchesWorldChange(PlayerData playerData) {
        return playerData.getMotionState().getTicksSinceWorldChange() < 40;
    }

    private static Boolean matchesFireball(PlayerData playerData) {
        MotionState motionState = playerData.getMotionState();
        return motionState.getTicksSinceFireball() < 25;
    }

    private static Boolean matchesPlacedSlime(PlayerData playerData) {
        return playerData.getMotionState().getTicksSinceSlimePlace() < 120;
    }

    private static Boolean matchesNotMoving(PlayerData playerData) {
        return !playerData.getCollisionState().isMoving();
    }

    private static Boolean matchesPartiallyStuck(PlayerData playerData) {
        return playerData.getCollisionState().isPartiallyStuck();
    }

    private static Boolean matchesPlacedClimbable(PlayerData playerData) {
        return playerData.getMotionState().getTicksSinceClimbablePlace() < 30;
    }

    private static Boolean matchesFullyStuck(PlayerData playerData) {
        return playerData.getCollisionState().isFullyStuck();
    }

    private static Boolean matchesSwimmingOnOldVersion(PlayerData playerData) {
        CollisionState collisionState = playerData.getCollisionState();
        if ((collisionState.getTicksSinceLiquid() < 60) && (playerData.getUser().getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_13)) && (ExemptionManager.isBefore1_13())) {
            return true;
        }
        return false;
    }

    private static Boolean matchesBlockPlaceFast(PlayerData playerData) {
        MotionState motionState = playerData.getMotionState();
        return motionState.getTicksSinceBlockPlace() < 15;
    }

    private static Boolean matchesBlockPlace(PlayerData playerData) {
        MotionState motionState = playerData.getMotionState();
        return motionState.getTicksSinceBlockPlace() < 30;
    }

    private static Boolean matchesCancelledPlace(PlayerData playerData) {
        return playerData.getMotionState().getTicksSinceCancelledPlace() < 40;
    }

    private static Boolean matchesServerPositionFast(PlayerData playerData) {
        MotionState motionState = playerData.getMotionState();
        return motionState.getTicksSinceServerPosition() < 5;
    }

    private static Boolean matchesServerPosition(PlayerData playerData) {
        MotionState motionState = playerData.getMotionState();
        return motionState.getTicksSinceServerPosition() < 8;
    }

    private static Boolean matchesPlacing(PlayerData playerData) {
        MotionState motionState = playerData.getMotionState();
        return motionState.getTicksSinceBlockPlace() < 10;
    }

    private static Boolean matchesBlockBreak(PlayerData playerData) {
        return playerData.getMotionState().getTicksSinceBlockBreak() < 25;
    }

    private static Boolean matchesDigging(PlayerData playerData) {
        if (!playerData.getMotionState().isDigging()) {
            TickProcessor tickProcessor = PluginServices.INSTANCE.getTickProcessor();
            int totalTicks = tickProcessor.getTotalTicks();
            if (totalTicks - playerData.getMotionState().getLastAbortDigTick() >= 20) {
                tickProcessor = PluginServices.INSTANCE.getTickProcessor();
                int totalTicks2 = tickProcessor.getTotalTicks();
                MotionState motionState = playerData.getMotionState();
                if (totalTicks2 - motionState.getLastDigTick() >= 20) {
                    return false;
                }
            }
        }
        return true;
    }

    private static Boolean matchesFrozen(PlayerData playerData) {
        CollisionState collisionState = playerData.getCollisionState();
        return collisionState.isFrozen();
    }

    private static Boolean matchesLevitation(PlayerData playerData) {
        if ((ExemptionManager.isAtLeast1_9()) && (playerData.getCollisionState().getTicksSinceLevitation() < 75)) {
            return true;
        }
        return false;
    }

    private static Boolean matchesDepthStrider(PlayerData playerData) {
        if (playerData.getMotionState().isDepthStrider()) {
            CollisionState collisionState = playerData.getCollisionState();
            if (collisionState.getTicksSinceLiquid() < 30) {
                return true;
            }
        }
        return false;
    }

    private static Boolean matchesDeath(PlayerData playerData) {
        return playerData.getMotionState().getTicksSinceDeath() < 25;
    }

    private static Boolean matchesEntityCollision(PlayerData playerData) {
        if (VulcanSettings.settingsEntityCollision) {
            CollisionState collisionState = playerData.getCollisionState();
            if (collisionState.getTicksSinceEntityCollision() < 8) {
                return true;
            }
        }
        return false;
    }

    private static Boolean matchesCauldron(PlayerData playerData) {
        return playerData.getCollisionState().isCauldron();
    }

    private static Boolean matchesSpectator(PlayerData playerData) {
        if (ExemptionManager.isAtLeast1_8()) {
            if ((playerData.getPlayer() != null) && (playerData.getPlayer().getGameMode() == GameMode.SPECTATOR)) {
                return true;
            }
            if ((playerData.getPlayer() != null) && (playerData.getPlayer().getGameMode() == GameMode.SPECTATOR)) {
                return true;
            }
        }
        return false;
    }

    private static Boolean matchesFirework(PlayerData playerData) {
        if (ExemptionManager.isAtLeast1_9()) {
            CollisionState collisionState = playerData.getCollisionState();
            if (collisionState.getTicksSinceFirework() < 150) {
                return true;
            }
        }
        return false;
    }

    private static Boolean matchesRiptide(PlayerData playerData) {
        if ((ExemptionManager.isNewerThan1_13()) && (playerData.getCollisionState().getTicksSinceRiptide() < 80)) {
            return true;
        }
        return false;
    }

    private static Boolean matchesPluginLoad(PlayerData playerData) {
        return System.currentTimeMillis() - PluginServices.INSTANCE.getStartedAtMillis() < 10000L;
    }

    private static Boolean matchesFishingRod(PlayerData playerData) {
        if (ExemptionManager.isAtLeast1_9()) {
            CollisionState collisionState = playerData.getCollisionState();
            if (collisionState.getTicksSinceFishingRod() < 100) {
                return true;
            }
        }
        return false;
    }

    private static Boolean matchesTrapdoor(PlayerData playerData) {
        if (playerData.getCollisionState().getTicksSinceTrapdoor() >= 20) {
            CollisionState collisionState = playerData.getCollisionState();
            if (!collisionState.isTrapdoor()) {
                return false;
            }
        }
        return true;
    }

    private static Boolean matchesSoulSpeed(PlayerData playerData) {
        if ((ExemptionManager.isAtLeast1_16()) && (playerData.getCollisionState().getTicksSinceSoulSpeed() < 60)) {
            return true;
        }
        return false;
    }

    private static Boolean matchesSleeping(PlayerData playerData) {
        if ((playerData.getPlayer() != null) && (playerData.getPlayer().isSleeping())) {
            return true;
        }
        TickProcessor tickProcessor = PluginServices.INSTANCE.getTickProcessor();
        int totalTicks = tickProcessor.getTotalTicks();
        if (totalTicks - playerData.getMotionState().getLastBedEnterTick() >= 40) {
            tickProcessor = PluginServices.INSTANCE.getTickProcessor();
            int totalTicks2 = tickProcessor.getTotalTicks();
            if (totalTicks2 - playerData.getMotionState().getLastBedLeaveTick() >= 40) {
                return false;
            }
        }
        return true;
    }

    private static Boolean matchesNearGround(PlayerData playerData) {
        return playerData.getCollisionState().isNearGround();
    }

    private static Boolean matchesEnderPearl(PlayerData playerData) {
        return playerData.getMotionState().getTicksSinceEnderPearl() < 5;
    }

    private static Boolean matchesChorusFruit(PlayerData playerData) {
        return playerData.getMotionState().getTicksSinceChorusFruit() < 25;
    }

    private static Boolean matchesPiston(PlayerData playerData) {
        if (!playerData.getCollisionState().isPiston()) {
            CollisionState collisionState = playerData.getCollisionState();
            if (collisionState.getTicksSincePiston() >= 20) {
                return false;
            }
        }
        return true;
    }

    private static Boolean matchesPlacedWeb(PlayerData playerData) {
        return playerData.getMotionState().getTicksSinceWebPlace() < 25;
    }

    private static Boolean matchesConduit(PlayerData playerData) {
        return playerData.getCollisionState().isConduit();
    }

    private static Boolean matchesSweetBerries(PlayerData playerData) {
        return playerData.getCollisionState().isSweetBerries();
    }

    private static Boolean matchesWall(PlayerData playerData) {
        return playerData.getCollisionState().isWall();
    }

    private static Boolean matchesHighSpeed(PlayerData playerData) {
        MotionState motionState = playerData.getMotionState();
        return motionState.getSpeedAmplifier() > 5;
    }

    private static Boolean matchesLilyPad(PlayerData playerData) {
        return playerData.getCollisionState().isLilyPad();
    }

    private static Boolean matchesHighJumpBoost(PlayerData playerData) {
        MotionState motionState = playerData.getMotionState();
        return motionState.getJumpBoostAmplifier() > 2;
    }

    private static Boolean matchesSlab(PlayerData playerData) {
        CollisionState collisionState = playerData.getCollisionState();
        return collisionState.isSlab();
    }

    private static Boolean matchesBed(PlayerData playerData) {
        CollisionState collisionState = playerData.getCollisionState();
        if ((!collisionState.isBed()) && (playerData.getCollisionState().getTicksSinceBed() >= 30)) {
            return false;
        }
        return true;
    }

    private static Boolean matchesIce(PlayerData playerData) {
        CollisionState collisionState = playerData.getCollisionState();
        if ((collisionState.getTicksSinceIce() >= 20) && (!playerData.getCollisionState().isIce())) {
            return false;
        }
        return true;
    }

    private static Boolean matchesRespawn(PlayerData playerData) {
        TickProcessor tickProcessor = PluginServices.INSTANCE.getTickProcessor();
        int totalTicks = tickProcessor.getTotalTicks();
        return totalTicks - playerData.getMotionState().getLastRespawnTick() < 20;
    }

    private static Boolean matchesFullySubmerged(PlayerData playerData) {
        return playerData.getCollisionState().isFullySubmerged();
    }

    private static Boolean matchesLaggedNearGroundModern(PlayerData playerData) {
        if (ExemptionManager.isNewerThan1_13()) {
            ConnectionTracker connectionTracker = playerData.getConnectionTracker();
            if (connectionTracker.getMovementDelayMillis() < 10L) {
                if (playerData.getCollisionState().isNearGroundWide()) {
                    return true;
                }
                if (!playerData.getCollisionState().hasNoGroundBeneath()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static Boolean matchesAnvil(PlayerData playerData) {
        return playerData.getCollisionState().isAnvil();
    }

    private static Boolean matchesPickedUpItem(PlayerData playerData) {
        TickProcessor tickProcessor = PluginServices.INSTANCE.getTickProcessor();
        int totalTicks = tickProcessor.getTotalTicks();
        return totalTicks - playerData.getMotionState().getLastItemPickupTick() < 30;
    }

    private static Boolean matchesDoor(PlayerData playerData) {
        CollisionState collisionState = playerData.getCollisionState();
        return collisionState.isDoor();
    }

    private static Boolean matchesChain(PlayerData playerData) {
        CollisionState collisionState = playerData.getCollisionState();
        return collisionState.isChain();
    }

    private static Boolean matchesHopper(PlayerData playerData) {
        return playerData.getCollisionState().isHopper();
    }

    private static Boolean matchesEndRod(PlayerData playerData) {
        return playerData.getCollisionState().isEndRod();
    }

    private static Boolean matchesEmptiedBucket(PlayerData playerData) {
        return playerData.getMotionState().getTicksSinceBucketEmpty() < 15;
    }

    private static Boolean matchesFlowerPot(PlayerData playerData) {
        CollisionState collisionState = playerData.getCollisionState();
        return collisionState.isFlowerPot();
    }

    private static Boolean matchesCarpet(PlayerData playerData) {
        return playerData.getCollisionState().isCarpet();
    }

    private static Boolean matchesCancelledMove(PlayerData playerData) {
        return playerData.getMotionState().getTicksSinceCancelledMove() < 5;
    }

    private static Boolean matchesCampfire(PlayerData playerData) {
        CollisionState collisionState = playerData.getCollisionState();
        return collisionState.isCampfire();
    }

    private static Boolean matchesNearSolid(PlayerData playerData) {
        return playerData.getCollisionState().isNearSolid();
    }

    private static Boolean matchesJoinedChunkLoad(PlayerData playerData) {
        if (System.currentTimeMillis() - playerData.getJoinTime() < 30000L) {
            CollisionState collisionState = playerData.getCollisionState();
            if (collisionState.getDeltaY() < -0.05F) {
                collisionState = playerData.getCollisionState();
                if (collisionState.getDeltaY() > -0.1F) {
                    return true;
                }
            }
        }
        return false;
    }

    private static Boolean matchesFastZero(PlayerData playerData) {
        CollisionState collisionState = playerData.getCollisionState();
        if ((collisionState.getDeltaY() == 0.0) && (playerData.getConnectionTracker().isFastMovement())) {
            return true;
        }
        return false;
    }

    private static Boolean matchesNetheriteArmor(PlayerData playerData) {
        return CheckUtils.isWearingNetheriteArmor(playerData);
    }

    private static Boolean matchesSnow(PlayerData playerData) {
        CollisionState collisionState = playerData.getCollisionState();
        return collisionState.isSnow();
    }

    private static Boolean matchesLaggedNearGround(PlayerData playerData) {
        if ((!playerData.getCollisionState().isNearGround()) && (playerData.getCollisionState().hasNoGroundBeneath())) {
            return false;
        }
        return playerData.getConnectionTracker().isFastMovement() ? true : false;
    }

    private static Boolean matchesJumpBoostRanOut(PlayerData playerData) {
        if ((playerData.getCollisionState().getTicksSinceJumpBoost() > 0) && (playerData.getCollisionState().getTicksSinceJumpBoost() < 30)) {
            return true;
        }
        return false;
    }

    private static Boolean matchesDragonDamage(PlayerData playerData) {
        return playerData.getMotionState().getTicksSinceDragonDamage() < 60;
    }

    private static Boolean matchesPowderSnow(PlayerData playerData) {
        CollisionState collisionState = playerData.getCollisionState();
        return collisionState.isPowderSnow();
    }

    private static Boolean matchesFenceGate(PlayerData playerData) {
        return playerData.getCollisionState().isFenceGate();
    }

    private static Boolean matchesHighLevitation(PlayerData playerData) {
        if (ExemptionManager.isAtLeast1_9()) {
            MotionState motionState = playerData.getMotionState();
            if (motionState.getLevitationAmplifier() > 10) {
                return true;
            }
        }
        return false;
    }

    private static Boolean matchesStairs(PlayerData playerData) {
        CollisionState collisionState = playerData.getCollisionState();
        return collisionState.isStairs();
    }

    private static Boolean matchesExplosion(PlayerData playerData) {
        VelocityTracker velocityTracker = playerData.getVelocityTracker();
        return velocityTracker.getTicksSinceExplosion() < 50;
    }

    private static Boolean matchesSkull(PlayerData playerData) {
        return playerData.getCollisionState().isSkull();
    }

    private static Boolean matchesAttackDamage(PlayerData playerData) {
        return playerData.getMotionState().getTicksSinceAttackDamage() < 80;
    }

    private static Boolean matchesChest(PlayerData playerData) {
        return playerData.getCollisionState().isChest();
    }

    private static Boolean matchesProjectileDamage(PlayerData playerData) {
        return playerData.getMotionState().getTicksSinceProjectileDamage() < 20;
    }

    private static Boolean matchesGlitchedBlocksAbove(PlayerData playerData) {
        if ((playerData.getCollisionState().getHeadMaterials() != null) && (!playerData.getCollisionState().getHeadMaterials().stream().allMatch(BlockUtils::isAir))) {
            return true;
        }
        return false;
    }

    private static Boolean matchesHighFlySpeed(PlayerData playerData) {
        return playerData.getCollisionState().getTicksSinceHighFlySpeed() < 100;
    }

    private static Boolean matchesDripstone(PlayerData playerData) {
        return playerData.getCollisionState().isDripstone();
    }

    private static Boolean matchesCollidingHorizontally(PlayerData playerData) {
        return playerData.getCollisionState().isCollidingHorizontally();
    }

    private static Boolean matchesSwimmingJesus(PlayerData playerData) {
        if (ExemptionManager.isAtLeast1_9()) {
            CollisionState collisionState = playerData.getCollisionState();
            if (collisionState.getTicksSinceSwimming() < 5) {
                return true;
            }
        }
        return false;
    }

    private static Boolean matchesSwimming(PlayerData playerData) {
        if (ExemptionManager.isAtLeast1_9()) {
            CollisionState collisionState = playerData.getCollisionState();
            if (collisionState.getTicksSinceSwimming() < 40) {
                return true;
            }
        }
        return false;
    }

    private static Boolean matchesPressurePlate(PlayerData playerData) {
        CollisionState collisionState = playerData.getCollisionState();
        return collisionState.isPressurePlate();
    }

    private static Boolean matchesSign(PlayerData playerData) {
        return playerData.getCollisionState().isSign();
    }

    private static Boolean matchesAroundSlime(PlayerData playerData) {
        CollisionState collisionState = playerData.getCollisionState();
        return collisionState.getTicksSinceNearbySlime() < 20;
    }

    private static Boolean matchesEntityCramFix(PlayerData playerData) {
        if ((VulcanSettings.settingsEntityCramFixEnabled) && (playerData.getCollisionState().getTicksSinceEntityCramming() < VulcanSettings.settingsEntityCramExemptTicks)) {
            return true;
        }
        return false;
    }

    private static Boolean matchesPacketExplosion(PlayerData playerData) {
        VelocityTracker velocityTracker = playerData.getVelocityTracker();
        return velocityTracker.getTicksSinceExplosion() < 16;
    }

    private static Boolean matchesHappyGhast(PlayerData playerData) {
        if ((!playerData.getCollisionState().isHappyGhast()) && (playerData.getCollisionState().getTicksSinceHappyGhast() >= 25)) {
            return false;
        }
        return true;
    }

    private static Boolean matchesBamboo(PlayerData playerData) {
        return playerData.getCollisionState().isBamboo();
    }

    private static Boolean matchesCake(PlayerData playerData) {
        CollisionState collisionState = playerData.getCollisionState();
        return collisionState.isCake();
    }

    private static Boolean matchesJoined(PlayerData playerData) {
        if ((System.currentTimeMillis() - playerData.getJoinTime() >= VulcanSettings.settingsJoinCheckWaitTime) && (playerData.getMotionState().getTicksSinceJoin() >= VulcanSettings.settingsMinTicksExisted)) {
            return false;
        }
        return true;
    }

    private static Boolean matchesChunk(PlayerData playerData) {
        if (playerData.getPlayer() != null) {
            World world2 = playerData.getPlayer().getWorld();
            CollisionState collisionState = playerData.getCollisionState();
            int index = NumberConversions.floor(collisionState.getX()) >> 4;
            CollisionState collisionState2 = playerData.getCollisionState();
            if (!world2.isChunkLoaded(index, NumberConversions.floor(collisionState2.getZ()) >> 4)) {
                return true;
            }
        }
        return false;
    }

    private static Boolean matchesTps(PlayerData playerData) {
        return ExemptionManager.getTps() < 19.25;
    }
}
