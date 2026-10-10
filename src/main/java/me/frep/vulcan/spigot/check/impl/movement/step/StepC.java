package me.frep.vulcan.spigot.check.impl.movement.step;

import com.github.retrooper.packetevents.event.PacketEvent;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptType;
import me.frep.vulcan.spigot.player.collision.CollisionState;
import me.frep.vulcan.spigot.player.movement.MotionState;
import me.frep.vulcan.spigot.player.session.PlayerData;

@CheckInfo(name = "Step", type = 'C', complexType = "Motion", description = "Reverse step.")
public class StepC extends AbstractCheck {

    public int nearGroundTicks;

    public StepC(PlayerData playerData) {
        super(playerData);
        this.nearGroundTicks = 0;
    }

    public void handlePacket(PacketEvent packetEvent) {
        boolean condition;
        if ((this.isPositionUpdatePacket(packetEvent)) && (!this.isTeleporting()) && (!this.isSetbackPending())) {
            if (this.playerData.getMotionState().getTicksSinceJoin() < 30) {
                return;
            }
            MotionState motionState = this.playerData.getMotionState();
            if (motionState.getAirDragModifier() != 1.0) {
                return;
            }
            CollisionState collisionState = this.playerData.getCollisionState();
            int airTicks = collisionState.getAirTicks();
            CollisionState collisionState2 = this.playerData.getCollisionState();
            double deltaY = collisionState2.getDeltaY();
            if (this.playerData.getMotionState().getDistanceFromBrokenBlock() < 1.25) {
                if (this.playerData.getMotionState().getDistanceFromBrokenBlock() > 0.0) {
                    return;
                }
            }
            float amount;
            if (this.playerData.getPlayer() == null) {
                amount = 0.0F;
            } else {
                amount = this.playerData.getPlayer().getFallDistance();
            }
            float distance;
            distance = amount;
            condition = (this.isExempt(new ExemptType[] { ExemptType.TELEPORT, ExemptType.WORLD_CHANGE, ExemptType.DOOR, ExemptType.DRAGON_DAMAGE, ExemptType.DEATH, ExemptType.JOINED, ExemptType.FLIGHT, ExemptType.CREATIVE, ExemptType.GLIDING, ExemptType.EXPLOSION, ExemptType.CANCELLED_PLACE, ExemptType.ELYTRA, ExemptType.LIQUID, ExemptType.VEHICLE, ExemptType.RIPTIDE, ExemptType.FISHING_ROD, ExemptType.SLEEPING, ExemptType.BLOCK_PLACE, ExemptType.VELOCITY, ExemptType.FAST, ExemptType.GLIDING, ExemptType.SLIME, ExemptType.ENDER_PEARL, ExemptType.ANVIL, ExemptType.END_ROD, ExemptType.CHAIN, ExemptType.DRIPSTONE, ExemptType.SERVER_POSITION, ExemptType.PISTON, ExemptType.JUMP_BOOST, ExemptType.CLIMBABLE, ExemptType.SCAFFOLDING, ExemptType.POWDER_SNOW, ExemptType.SNOW, ExemptType.SLOW_FALLING })) || (((this.playerData.getCollisionState()).isAmethyst()) || ((this.playerData.getCollisionState().isDripstone()) || (!((this.playerData.getMotionState()).getTicksSinceCrystalDamage() >= 20))));
            boolean condition2 = condition;
            boolean nearGround = this.playerData.getCollisionState().isNearGround();
            if (nearGround) {
                this.nearGroundTicks++;
            } else {
                this.nearGroundTicks = 0;
            }
            if (!condition2) {
                switch(airTicks) {
                    case 0:
                        if (deltaY < -0.5 && this.nearGroundTicks > 5) {
                            if (this.increaseBuffer() > this.maximumBuffer) {
                                this.fail("ticks=" + airTicks + " deltaY=" + deltaY + " distance=" + distance + " ng=" + this.nearGroundTicks);
                            }
                        } else {
                            this.decayBuffer();
                        }
                        break;
                    case 1:
                        if (deltaY < -0.0785F) {
                            if (this.increaseBuffer() > this.maximumBuffer) {
                                this.fail("ticks=" + airTicks + " deltaY=" + deltaY + " distance=" + distance);
                            }
                        } else {
                            this.decayBuffer();
                        }
                        break;
                    case 2:
                        if (deltaY < -0.1553F) {
                            if (this.increaseBuffer() > this.maximumBuffer) {
                                this.fail("ticks=" + airTicks + " deltaY=" + deltaY + " distance=" + distance);
                            }
                        } else {
                            this.decayBuffer();
                        }
                        break;
                    case 3:
                        if (!(deltaY < -0.2306F)) {
                            this.decayBuffer();
                            break;
                        } else if (this.increaseBuffer() > this.maximumBuffer) {
                            this.fail("ticks=" + airTicks + " deltaY=" + deltaY + " distance=" + distance);
                        }
                    case 4:
                        if (!(deltaY < -0.3044F)) {
                            this.decayBuffer();
                            break;
                        } else if (this.increaseBuffer() > this.maximumBuffer) {
                            this.fail("ticks=" + airTicks + " deltaY=" + deltaY + " distance=" + distance);
                        }
                    case 5:
                        if (!(deltaY < -0.3768F)) {
                            this.decayBuffer();
                            break;
                        } else if (this.increaseBuffer() > this.maximumBuffer) {
                            this.fail("ticks=" + airTicks + " deltaY=" + deltaY + " distance=" + distance);
                        }
                    case 6:
                        if (!(deltaY < -0.4475F)) {
                            this.decayBuffer();
                            break;
                        } else if (this.increaseBuffer() > this.maximumBuffer) {
                            this.fail("ticks=" + airTicks + " deltaY=" + deltaY + " distance=" + distance);
                        }
                    case 7:
                        if (!(deltaY < -0.5171F)) {
                            this.decayBuffer();
                            break;
                        } else if (this.increaseBuffer() > this.maximumBuffer) {
                            this.fail("ticks=" + airTicks + " deltaY=" + deltaY + " distance=" + distance);
                        }
                    case 8:
                        if (!(deltaY < -0.586F)) {
                            this.decayBuffer();
                            break;
                        } else if (this.increaseBuffer() > this.maximumBuffer) {
                            this.fail("ticks=" + airTicks + " deltaY=" + deltaY + " distance=" + distance);
                        }
                    case 9:
                        if (!(deltaY < -0.652F)) {
                            this.decayBuffer();
                            break;
                        } else if (this.increaseBuffer() > this.maximumBuffer) {
                            this.fail("ticks=" + airTicks + " deltaY=" + deltaY + " distance=" + distance);
                        }
                    case 10:
                        if (!(deltaY < -0.7171F)) {
                            this.decayBuffer();
                            break;
                        } else if (this.increaseBuffer() > this.maximumBuffer) {
                            this.fail("ticks=" + airTicks + " deltaY=" + deltaY + " distance=" + distance);
                        }
                    case 11:
                        if (!(deltaY < -0.7812F)) {
                            this.decayBuffer();
                            break;
                        } else if (this.increaseBuffer() > this.maximumBuffer) {
                            this.fail("ticks=" + airTicks + " deltaY=" + deltaY + " distance=" + distance);
                        }
                    case 12:
                        if (!(deltaY < -0.844F)) {
                            this.decayBuffer();
                            break;
                        } else if (this.increaseBuffer() > this.maximumBuffer) {
                            this.fail("ticks=" + airTicks + " deltaY=" + deltaY + " distance=" + distance);
                        }
                    case 13:
                        if (!(deltaY < -0.9055F)) {
                            this.decayBuffer();
                            break;
                        } else if (this.increaseBuffer() > this.maximumBuffer) {
                            this.fail("ticks=" + airTicks + " deltaY=" + deltaY + " distance=" + distance);
                        }
                    case 14:
                        if (!(deltaY < -0.9658F)) {
                            this.decayBuffer();
                            break;
                        } else if (this.increaseBuffer() > this.maximumBuffer) {
                            this.fail("ticks=" + airTicks + " deltaY=" + deltaY + " distance=" + distance);
                        }
                    case 15:
                        if (!(deltaY < -1.0249F)) {
                            this.decayBuffer();
                            break;
                        } else if (this.increaseBuffer() > this.maximumBuffer) {
                            this.fail("ticks=" + airTicks + " deltaY=" + deltaY + " distance=" + distance);
                        }
                    case 16:
                        if (!(deltaY < -1.0828F)) {
                            this.decayBuffer();
                            break;
                        } else if (this.increaseBuffer() > this.maximumBuffer) {
                            this.fail("ticks=" + airTicks + " deltaY=" + deltaY + " distance=" + distance);
                        }
                    case 17:
                        if (!(deltaY < -1.1395F)) {
                            this.decayBuffer();
                            break;
                        } else if (this.increaseBuffer() > this.maximumBuffer) {
                            this.fail("ticks=" + airTicks + " deltaY=" + deltaY + " distance=" + distance);
                        }
                    case 18:
                        if (!(deltaY < -1.1951F)) {
                            this.decayBuffer();
                            break;
                        } else if (this.increaseBuffer() > this.maximumBuffer) {
                            this.fail("ticks=" + airTicks + " deltaY=" + deltaY + " distance=" + distance);
                        }
                    case 19:
                        if (!(deltaY < -1.2496F)) {
                            this.decayBuffer();
                            break;
                        } else if (this.increaseBuffer() > this.maximumBuffer) {
                            this.fail("ticks=" + airTicks + " deltaY=" + deltaY + " distance=" + distance);
                        }
                    case 20:
                        if (!(deltaY < -1.303F)) {
                            this.decayBuffer();
                            break;
                        } else if (this.increaseBuffer() > this.maximumBuffer) {
                            this.fail("ticks=" + airTicks + " deltaY=" + deltaY + " distance=" + distance);
                        }
                    case 21:
                        if (!(deltaY < -1.356F)) {
                            this.decayBuffer();
                            break;
                        } else if (this.increaseBuffer() > this.maximumBuffer) {
                            this.fail("ticks=" + airTicks + " deltaY=" + deltaY + " distance=" + distance);
                        }
                    case 22:
                        if (!(deltaY < -1.407F)) {
                            this.decayBuffer();
                            break;
                        } else if (this.increaseBuffer() > this.maximumBuffer) {
                            this.fail("ticks=" + airTicks + " deltaY=" + deltaY + " distance=" + distance);
                        }
                    case 23:
                        if (!(deltaY < -1.457F)) {
                            this.decayBuffer();
                            break;
                        } else if (this.increaseBuffer() > this.maximumBuffer) {
                            this.fail("ticks=" + airTicks + " deltaY=" + deltaY + " distance=" + distance);
                        }
                    case 24:
                        if (!(deltaY < -1.507F)) {
                            this.decayBuffer();
                            break;
                        } else if (this.increaseBuffer() > this.maximumBuffer) {
                            this.fail("ticks=" + airTicks + " deltaY=" + deltaY + " distance=" + distance);
                        }
                    case 25:
                        if (deltaY < -1.555F) {
                            if (this.increaseBuffer() > this.maximumBuffer) {
                                this.fail("ticks=" + airTicks + " deltaY=" + deltaY + " distance=" + distance);
                                long longValue;
                                int index = (longValue = 0L) == 0L ? 0 : (longValue < 0L ? -1 : 1);
                            }
                        } else {
                            this.decayBuffer();
                        }
                }
            }
        }
    }

    @Override
    public void update() {
    }
}
