package me.frep.vulcan.spigot.check.manager;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import me.frep.vulcan.spigot.check.AbstractCheck;
import me.frep.vulcan.spigot.check.api.CheckInfo;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.check.impl.combat.aim.rotation.AimA;
import me.frep.vulcan.spigot.check.impl.combat.aim.sensitivity.AimB;
import me.frep.vulcan.spigot.check.impl.combat.aim.pattern.AimC;
import me.frep.vulcan.spigot.check.impl.combat.aim.rotation.AimD;
import me.frep.vulcan.spigot.check.impl.combat.aim.rotation.AimE;
import me.frep.vulcan.spigot.check.impl.combat.aim.rotation.AimF;
import me.frep.vulcan.spigot.check.impl.combat.aim.rotation.AimG;
import me.frep.vulcan.spigot.check.impl.combat.aim.sensitivity.AimH;
import me.frep.vulcan.spigot.check.impl.combat.aim.pattern.AimI;
import me.frep.vulcan.spigot.check.impl.combat.aim.pattern.AimK;
import me.frep.vulcan.spigot.check.impl.combat.aim.rotation.AimL;
import me.frep.vulcan.spigot.check.impl.combat.aim.rotation.AimN;
import me.frep.vulcan.spigot.check.impl.combat.aim.rotation.AimO;
import me.frep.vulcan.spigot.check.impl.combat.aim.rotation.AimP;
import me.frep.vulcan.spigot.check.impl.combat.aim.sensitivity.AimQ;
import me.frep.vulcan.spigot.check.impl.combat.aim.sensitivity.AimS;
import me.frep.vulcan.spigot.check.impl.combat.aim.sensitivity.AimU;
import me.frep.vulcan.spigot.check.impl.combat.aim.pattern.AimW;
import me.frep.vulcan.spigot.check.impl.combat.aim.pattern.AimX;
import me.frep.vulcan.spigot.check.impl.combat.aim.rotation.AimY;
import me.frep.vulcan.spigot.check.impl.combat.autoblock.AutoBlockA;
import me.frep.vulcan.spigot.check.impl.combat.autoblock.AutoBlockB;
import me.frep.vulcan.spigot.check.impl.combat.autoblock.AutoBlockC;
import me.frep.vulcan.spigot.check.impl.combat.autoblock.AutoBlockD;
import me.frep.vulcan.spigot.check.impl.combat.autoclicker.rate.AutoClickerA;
import me.frep.vulcan.spigot.check.impl.combat.autoclicker.distribution.AutoClickerB;
import me.frep.vulcan.spigot.check.impl.combat.autoclicker.rate.AutoClickerC;
import me.frep.vulcan.spigot.check.impl.combat.autoclicker.distribution.AutoClickerD;
import me.frep.vulcan.spigot.check.impl.combat.autoclicker.distribution.AutoClickerE;
import me.frep.vulcan.spigot.check.impl.combat.autoclicker.distribution.AutoClickerF;
import me.frep.vulcan.spigot.check.impl.combat.autoclicker.distribution.AutoClickerG;
import me.frep.vulcan.spigot.check.impl.combat.autoclicker.consistency.AutoClickerH;
import me.frep.vulcan.spigot.check.impl.combat.autoclicker.distribution.AutoClickerI;
import me.frep.vulcan.spigot.check.impl.combat.autoclicker.distribution.AutoClickerJ;
import me.frep.vulcan.spigot.check.impl.combat.autoclicker.consistency.AutoClickerK;
import me.frep.vulcan.spigot.check.impl.combat.autoclicker.consistency.AutoClickerL;
import me.frep.vulcan.spigot.check.impl.combat.autoclicker.consistency.AutoClickerM;
import me.frep.vulcan.spigot.check.impl.combat.autoclicker.consistency.AutoClickerN;
import me.frep.vulcan.spigot.check.impl.combat.autoclicker.rate.AutoClickerO;
import me.frep.vulcan.spigot.check.impl.combat.autoclicker.consistency.AutoClickerP;
import me.frep.vulcan.spigot.check.impl.combat.autoclicker.consistency.AutoClickerQ;
import me.frep.vulcan.spigot.check.impl.combat.autoclicker.consistency.AutoClickerR;
import me.frep.vulcan.spigot.check.impl.combat.autoclicker.distribution.AutoClickerS;
import me.frep.vulcan.spigot.check.impl.combat.autoclicker.distribution.AutoClickerT;
import me.frep.vulcan.spigot.check.impl.combat.criticals.CriticalsA;
import me.frep.vulcan.spigot.check.impl.combat.criticals.CriticalsB;
import me.frep.vulcan.spigot.check.impl.combat.fastbow.FastBowA;
import me.frep.vulcan.spigot.check.impl.combat.hitbox.HitboxA;
import me.frep.vulcan.spigot.check.impl.combat.hitbox.HitboxB;
import me.frep.vulcan.spigot.check.impl.combat.killaura.KillAuraA;
import me.frep.vulcan.spigot.check.impl.combat.killaura.KillAuraB;
import me.frep.vulcan.spigot.check.impl.combat.killaura.KillAuraC;
import me.frep.vulcan.spigot.check.impl.combat.killaura.KillAuraD;
import me.frep.vulcan.spigot.check.impl.combat.killaura.KillAuraJ;
import me.frep.vulcan.spigot.check.impl.combat.killaura.KillAuraK;
import me.frep.vulcan.spigot.check.impl.combat.killaura.KillAuraL;
import me.frep.vulcan.spigot.check.impl.combat.reach.ReachA;
import me.frep.vulcan.spigot.check.impl.combat.reach.ReachB;
import me.frep.vulcan.spigot.check.impl.combat.velocity.VelocityA;
import me.frep.vulcan.spigot.check.impl.combat.velocity.VelocityB;
import me.frep.vulcan.spigot.check.impl.combat.velocity.VelocityC;
import me.frep.vulcan.spigot.check.impl.combat.velocity.VelocityD;
import me.frep.vulcan.spigot.check.impl.movement.antilevitation.AntiLevitationA;
import me.frep.vulcan.spigot.check.impl.movement.boatfly.BoatFlyA;
import me.frep.vulcan.spigot.check.impl.movement.boatfly.BoatFlyB;
import me.frep.vulcan.spigot.check.impl.movement.boatfly.BoatFlyC;
import me.frep.vulcan.spigot.check.impl.movement.elytra.ElytraA;
import me.frep.vulcan.spigot.check.impl.movement.elytra.ElytraB;
import me.frep.vulcan.spigot.check.impl.movement.elytra.ElytraC;
import me.frep.vulcan.spigot.check.impl.movement.elytra.ElytraF;
import me.frep.vulcan.spigot.check.impl.movement.elytra.ElytraG;
import me.frep.vulcan.spigot.check.impl.movement.elytra.ElytraI;
import me.frep.vulcan.spigot.check.impl.movement.elytra.ElytraK;
import me.frep.vulcan.spigot.check.impl.movement.elytra.ElytraL;
import me.frep.vulcan.spigot.check.impl.movement.elytra.ElytraM;
import me.frep.vulcan.spigot.check.impl.movement.elytra.ElytraN;
import me.frep.vulcan.spigot.check.impl.movement.entityflight.EntityFlightA;
import me.frep.vulcan.spigot.check.impl.movement.entityflight.EntityFlightB;
import me.frep.vulcan.spigot.check.impl.movement.entityspeed.EntitySpeedA;
import me.frep.vulcan.spigot.check.impl.movement.fastclimb.FastClimbA;
import me.frep.vulcan.spigot.check.impl.movement.flight.FlightA;
import me.frep.vulcan.spigot.check.impl.movement.flight.FlightB;
import me.frep.vulcan.spigot.check.impl.movement.flight.FlightC;
import me.frep.vulcan.spigot.check.impl.movement.flight.FlightD;
import me.frep.vulcan.spigot.check.impl.movement.flight.FlightE;
import me.frep.vulcan.spigot.check.impl.movement.flight.FlightF;
import me.frep.vulcan.spigot.check.impl.movement.jesus.JesusA;
import me.frep.vulcan.spigot.check.impl.movement.jesus.JesusB;
import me.frep.vulcan.spigot.check.impl.movement.jesus.JesusC;
import me.frep.vulcan.spigot.check.impl.movement.jesus.JesusD;
import me.frep.vulcan.spigot.check.impl.movement.jesus.JesusE;
import me.frep.vulcan.spigot.check.impl.movement.jump.JumpA;
import me.frep.vulcan.spigot.check.impl.movement.jump.JumpB;
import me.frep.vulcan.spigot.check.impl.movement.jump.JumpC;
import me.frep.vulcan.spigot.check.impl.movement.motion.MotionA;
import me.frep.vulcan.spigot.check.impl.movement.motion.MotionB;
import me.frep.vulcan.spigot.check.impl.movement.motion.MotionC;
import me.frep.vulcan.spigot.check.impl.movement.motion.MotionE;
import me.frep.vulcan.spigot.check.impl.movement.nosaddle.NoSaddleA;
import me.frep.vulcan.spigot.check.impl.movement.noslow.NoSlowA;
import me.frep.vulcan.spigot.check.impl.movement.noslow.NoSlowB;
import me.frep.vulcan.spigot.check.impl.movement.noslow.NoSlowC;
import me.frep.vulcan.spigot.check.impl.movement.speed.SpeedA;
import me.frep.vulcan.spigot.check.impl.movement.speed.SpeedB;
import me.frep.vulcan.spigot.check.impl.movement.speed.SpeedC;
import me.frep.vulcan.spigot.check.impl.movement.speed.SpeedD;
import me.frep.vulcan.spigot.check.impl.movement.speed.SpeedE;
import me.frep.vulcan.spigot.check.impl.movement.sprint.SprintA;
import me.frep.vulcan.spigot.check.impl.movement.step.StepA;
import me.frep.vulcan.spigot.check.impl.movement.step.StepC;
import me.frep.vulcan.spigot.check.impl.movement.strafe.StrafeA;
import me.frep.vulcan.spigot.check.impl.movement.vclip.VClipA;
import me.frep.vulcan.spigot.check.impl.movement.wallclimb.WallClimbA;
import me.frep.vulcan.spigot.check.impl.player.airplace.AirPlaceA;
import me.frep.vulcan.spigot.check.impl.player.badpackets.blocks.BadPackets5;
import me.frep.vulcan.spigot.check.impl.player.badpackets.blocks.BadPackets6;
import me.frep.vulcan.spigot.check.impl.player.badpackets.combat.BadPackets7;
import me.frep.vulcan.spigot.check.impl.player.badpackets.blocks.BadPackets8;
import me.frep.vulcan.spigot.check.impl.player.badpackets.combat.BadPackets9;
import me.frep.vulcan.spigot.check.impl.player.badpackets.state.BadPacketsA;
import me.frep.vulcan.spigot.check.impl.player.badpackets.movement.BadPacketsB;
import me.frep.vulcan.spigot.check.impl.player.badpackets.movement.BadPacketsC;
import me.frep.vulcan.spigot.check.impl.player.badpackets.movement.BadPacketsD;
import me.frep.vulcan.spigot.check.impl.player.badpackets.interaction.BadPacketsE;
import me.frep.vulcan.spigot.check.impl.player.badpackets.interaction.BadPacketsF;
import me.frep.vulcan.spigot.check.impl.player.badpackets.inventory.BadPacketsG;
import me.frep.vulcan.spigot.check.impl.player.badpackets.sequence.BadPacketsH;
import me.frep.vulcan.spigot.check.impl.player.badpackets.interaction.BadPacketsI;
import me.frep.vulcan.spigot.check.impl.player.badpackets.inventory.BadPacketsJ;
import me.frep.vulcan.spigot.check.impl.player.badpackets.state.BadPacketsK;
import me.frep.vulcan.spigot.check.impl.player.badpackets.sequence.BadPacketsM;
import me.frep.vulcan.spigot.check.impl.player.badpackets.sequence.BadPacketsN;
import me.frep.vulcan.spigot.check.impl.player.badpackets.inventory.BadPacketsO;
import me.frep.vulcan.spigot.check.impl.player.badpackets.sequence.BadPacketsP;
import me.frep.vulcan.spigot.check.impl.player.badpackets.inventory.BadPacketsQ;
import me.frep.vulcan.spigot.check.impl.player.badpackets.sequence.BadPacketsR;
import me.frep.vulcan.spigot.check.impl.player.badpackets.connection.BadPacketsT;
import me.frep.vulcan.spigot.check.impl.player.badpackets.interaction.BadPacketsV;
import me.frep.vulcan.spigot.check.impl.player.badpackets.inventory.BadPacketsW;
import me.frep.vulcan.spigot.check.impl.player.badpackets.sequence.BadPacketsX;
import me.frep.vulcan.spigot.check.impl.player.badpackets.movement.BadPacketsY;
import me.frep.vulcan.spigot.check.impl.player.badpackets.state.BadPacketsZ;
import me.frep.vulcan.spigot.check.impl.player.baritone.BaritoneA;
import me.frep.vulcan.spigot.check.impl.player.baritone.BaritoneB;
import me.frep.vulcan.spigot.check.impl.player.fastbreak.FastBreakA;
import me.frep.vulcan.spigot.check.impl.player.fastplace.FastPlaceA;
import me.frep.vulcan.spigot.check.impl.player.ghosthand.GhostHandA;
import me.frep.vulcan.spigot.check.impl.player.groundspoof.GroundSpoofA;
import me.frep.vulcan.spigot.check.impl.player.groundspoof.GroundSpoofB;
import me.frep.vulcan.spigot.check.impl.player.groundspoof.GroundSpoofC;
import me.frep.vulcan.spigot.check.impl.player.improbable.ImprobableA;
import me.frep.vulcan.spigot.check.impl.player.improbable.ImprobableB;
import me.frep.vulcan.spigot.check.impl.player.improbable.ImprobableC;
import me.frep.vulcan.spigot.check.impl.player.improbable.ImprobableD;
import me.frep.vulcan.spigot.check.impl.player.improbable.ImprobableE;
import me.frep.vulcan.spigot.check.impl.player.improbable.ImprobableF;
import me.frep.vulcan.spigot.check.impl.player.invalid.InvalidA;
import me.frep.vulcan.spigot.check.impl.player.invalid.InvalidB;
import me.frep.vulcan.spigot.check.impl.player.invalid.InvalidC;
import me.frep.vulcan.spigot.check.impl.player.invalid.InvalidE;
import me.frep.vulcan.spigot.check.impl.player.invalid.InvalidF;
import me.frep.vulcan.spigot.check.impl.player.invalid.InvalidI;
import me.frep.vulcan.spigot.check.impl.player.invalid.InvalidJ;
import me.frep.vulcan.spigot.check.impl.player.scaffold.ScaffoldA;
import me.frep.vulcan.spigot.check.impl.player.scaffold.ScaffoldB;
import me.frep.vulcan.spigot.check.impl.player.scaffold.ScaffoldC;
import me.frep.vulcan.spigot.check.impl.player.scaffold.ScaffoldD;
import me.frep.vulcan.spigot.check.impl.player.scaffold.ScaffoldE;
import me.frep.vulcan.spigot.check.impl.player.scaffold.ScaffoldF;
import me.frep.vulcan.spigot.check.impl.player.scaffold.ScaffoldG;
import me.frep.vulcan.spigot.check.impl.player.scaffold.ScaffoldH;
import me.frep.vulcan.spigot.check.impl.player.scaffold.ScaffoldI;
import me.frep.vulcan.spigot.check.impl.player.scaffold.ScaffoldJ;
import me.frep.vulcan.spigot.check.impl.player.scaffold.ScaffoldK;
import me.frep.vulcan.spigot.check.impl.player.scaffold.ScaffoldM;
import me.frep.vulcan.spigot.check.impl.player.scaffold.ScaffoldN;
import me.frep.vulcan.spigot.check.impl.player.timer.TimerA;
import me.frep.vulcan.spigot.check.impl.player.timer.TimerD;
import me.frep.vulcan.spigot.check.impl.player.tower.TowerA;
import me.frep.vulcan.spigot.config.VulcanSettings;
import me.frep.vulcan.spigot.player.session.PlayerData;

public class CheckManager {

    public static final List<Constructor<?>> constructors = new ArrayList<>();

    public static final Class[] checkTypes = new Class[] { AimA.class, AimB.class, AimC.class, AimD.class, AimE.class, AimF.class, AimG.class, AimH.class, AimI.class, AimK.class, AimL.class, AimN.class, AimO.class, AimP.class, AimQ.class, AimS.class, AimU.class, AimW.class, AimX.class, AimY.class, AutoBlockA.class, AutoBlockB.class, AutoBlockC.class, AutoBlockD.class, AutoClickerA.class, AutoClickerB.class, AutoClickerC.class, AutoClickerD.class, AutoClickerE.class, AutoClickerF.class, AutoClickerG.class, AutoClickerH.class, AutoClickerI.class, AutoClickerJ.class, AutoClickerK.class, AutoClickerL.class, AutoClickerM.class, AutoClickerN.class, AutoClickerO.class, AutoClickerP.class, AutoClickerQ.class, AutoClickerR.class, AutoClickerS.class, AutoClickerT.class, FastBowA.class, HitboxA.class, HitboxB.class, KillAuraA.class, KillAuraB.class, KillAuraC.class, KillAuraD.class, KillAuraJ.class, KillAuraK.class, KillAuraL.class, ReachA.class, ReachB.class, VelocityA.class, VelocityB.class, VelocityC.class, VelocityD.class, BoatFlyA.class, BoatFlyB.class, BoatFlyC.class, AntiLevitationA.class, NoSaddleA.class, EntitySpeedA.class, EntityFlightA.class, EntityFlightB.class, GhostHandA.class, ElytraA.class, ElytraB.class, ElytraC.class, ElytraF.class, ElytraG.class, ElytraI.class, ElytraK.class, ElytraL.class, ElytraM.class, ElytraN.class, CriticalsA.class, CriticalsB.class, FastClimbA.class, FlightA.class, FlightB.class, FlightC.class, FlightD.class, FlightE.class, FlightF.class, JesusA.class, JesusB.class, JesusC.class, JesusD.class, JesusE.class, JumpA.class, JumpB.class, JumpC.class, MotionA.class, MotionB.class, MotionC.class, MotionE.class, NoSlowA.class, NoSlowB.class, NoSlowC.class, SpeedA.class, SpeedB.class, SpeedC.class, SpeedD.class, SpeedE.class, StepA.class, StepC.class, SprintA.class, StrafeA.class, WallClimbA.class, BaritoneA.class, BaritoneB.class, BadPackets5.class, BadPackets6.class, BadPackets7.class, BadPackets8.class, BadPackets9.class, BadPacketsA.class, BadPacketsB.class, BadPacketsC.class, BadPacketsD.class, BadPacketsE.class, BadPacketsF.class, BadPacketsG.class, BadPacketsH.class, BadPacketsI.class, BadPacketsJ.class, BadPacketsK.class, BadPacketsM.class, BadPacketsN.class, BadPacketsO.class, BadPacketsP.class, BadPacketsQ.class, BadPacketsR.class, BadPacketsT.class, BadPacketsV.class, BadPacketsW.class, BadPacketsX.class, BadPacketsY.class, BadPacketsZ.class, FastPlaceA.class, FastBreakA.class, GroundSpoofA.class, GroundSpoofB.class, GroundSpoofC.class, ImprobableA.class, ImprobableB.class, ImprobableC.class, ImprobableD.class, ImprobableE.class, ImprobableF.class, InvalidA.class, InvalidB.class, InvalidC.class, InvalidE.class, InvalidF.class, InvalidI.class, InvalidJ.class, VClipA.class, AirPlaceA.class, ScaffoldA.class, ScaffoldB.class, ScaffoldC.class, ScaffoldD.class, ScaffoldE.class, ScaffoldF.class, ScaffoldG.class, ScaffoldH.class, ScaffoldI.class, ScaffoldJ.class, ScaffoldK.class, ScaffoldM.class, ScaffoldN.class, TimerA.class, TimerD.class, TowerA.class };

    public static List<AbstractCheck> createChecks(PlayerData data) {
        List<AbstractCheck> checks = new ArrayList<>(constructors.size());
        for (Constructor<?> constructor : constructors) {
            try {
                checks.add((AbstractCheck) constructor.newInstance(data));
            } catch (ReflectiveOperationException failure) {
                throw new IllegalStateException("Cannot create check " + constructor.getDeclaringClass().getName(), failure);
            }
        }
        return checks;
    }

    public static void loadEnabledChecks() {
        constructors.clear();
        for (Class<?> type : checkTypes) {
            CheckInfo information = type.getAnnotation(CheckInfo.class);
            if (information == null) {
                ExemptionManager.logInfo("Failed to load check of class " + type.getSimpleName() + " [CheckInfo not present]");
                continue;
            }
            String checkId = (information.name() + information.type()).replace(" ", "");
            if (!Boolean.TRUE.equals(VulcanSettings.checkEnabled.get(checkId)))
                continue;
            try {
                constructors.add(type.getConstructor(PlayerData.class));
            } catch (NoSuchMethodException failure) {
                throw new IllegalStateException("Check has no PlayerData constructor: " + type.getName(), failure);
            }
        }
    }
}
