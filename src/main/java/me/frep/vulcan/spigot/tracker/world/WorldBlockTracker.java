package me.frep.vulcan.spigot.tracker.world;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Client;
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Server;
import com.github.retrooper.packetevents.protocol.world.BlockFace;
import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.protocol.world.chunk.Column;
import com.github.retrooper.packetevents.protocol.world.chunk.TileEntity;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.enums.PotentSulfurState;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkDataBulk;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerMultiBlockChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUnloadChunk;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerMultiBlockChange.EncodedBlock;
import java.util.HashMap;
import java.util.Map;
import me.frep.vulcan.spigot.check.exemption.ExemptionManager;
import me.frep.vulcan.spigot.player.session.PlayerData;
import me.frep.vulcan.spigot.geometry.position.BlockPosition;

public class WorldBlockTracker {

    private final PlayerData playerData;

    private final Map<Long, BaseChunk[]> chunkData = new HashMap<>();

    private final Map<Long, Map<Long, BlockPosition>> trackedBlocks = new HashMap<>();

    public WorldBlockTracker(PlayerData playerData) {
        this.playerData = playerData;
    }

    public void handleBlockPlace(PacketReceiveEvent event) {
        if (event.getPacketType() == Client.PLAYER_BLOCK_PLACEMENT) {
            this.applyPlacedBlock(new WrapperPlayClientPlayerBlockPlacement(event));
        }
    }

    public void dispatchServerPacket(PacketSendEvent event) {
        if (event.getPacketType() == Server.JOIN_GAME || event.getPacketType() == Server.RESPAWN) {
            this.reset();
            return;
        }
        if (event.getPacketType() == Server.CHUNK_DATA) {
            this.handleChunkData(new WrapperPlayServerChunkData(event));
        } else if (event.getPacketType() == Server.MAP_CHUNK_BULK) {
            this.handleBulkChunkData(new WrapperPlayServerChunkDataBulk(event));
        } else if (event.getPacketType() == Server.UNLOAD_CHUNK) {
            this.handleUnloadChunk(new WrapperPlayServerUnloadChunk(event));
        } else if (event.getPacketType() == Server.BLOCK_CHANGE) {
            this.handleBlockChangePacket(new WrapperPlayServerBlockChange(event));
        } else if (event.getPacketType() == Server.MULTI_BLOCK_CHANGE) {
            this.handleMultiBlockChange(new WrapperPlayServerMultiBlockChange(event));
        }
    }

    private void handleChunkData(WrapperPlayServerChunkData packet) {
        Column column = packet.getColumn();
        int chunkX = column.getX();
        int chunkZ = column.getZ();
        boolean fullChunk = column.isFullChunk();
        BaseChunk[] sections = column.getChunks();
        TileEntity[] tileEntities = column.getTileEntities();
        this.playerData.getConfirmationTracker().runAfterConfirmation(() -> this.applyChunkData(chunkX, chunkZ, fullChunk, sections, tileEntities));
    }

    private void handleBulkChunkData(WrapperPlayServerChunkDataBulk packet) {
        int[] chunkXs = packet.getX();
        int[] chunkZs = packet.getZ();
        BaseChunk[][] chunks = packet.getChunks();
        this.playerData.getConfirmationTracker().runAfterConfirmation(() -> this.applyBulkChunkData(chunkXs, chunkZs, chunks));
    }

    private void handleUnloadChunk(WrapperPlayServerUnloadChunk packet) {
        int chunkX = packet.getChunkX();
        int chunkZ = packet.getChunkZ();
        this.playerData.getConfirmationTracker().runAfterConfirmation(() -> this.removeChunk(chunkX, chunkZ));
    }

    private void handleBlockChangePacket(WrapperPlayServerBlockChange packet) {
        Vector3i position = packet.getBlockPosition();
        WrappedBlockState blockState = packet.getBlockState();
        this.playerData.getConfirmationTracker().runAfterConfirmation(() -> this.applyBlockChange(position, blockState));
    }

    private void handleMultiBlockChange(WrapperPlayServerMultiBlockChange packet) {
        EncodedBlock[] blocks = packet.getBlocks();
        this.playerData.getConfirmationTracker().runAfterConfirmation(() -> this.applyBlockChanges(blocks));
    }

    private void applyPlacedBlock(WrapperPlayClientPlayerBlockPlacement packet) {
        if (packet.getItemStack().isPresent() && packet.getItemStack().get().getType().getPlacedType() != null) {
            Vector3i position = this.getPosition(packet.getBlockPosition(), packet.getFace());
            WrappedBlockState blockState = packet.getItemStack().get().getType().getPlacedType().createBlockState(PacketEvents.getAPI().getServerManager().getVersion().toClientVersion());
            this.cacheBlockChange(position.getX(), position.getY(), position.getZ(), blockState);
        }
    }

    private void cacheBlockChange(int x, int y, int z, WrappedBlockState blockState) {
        int minY = this.getMinY();
        int sectionCount = this.getSectionCount();
        int sectionIndex = (y - minY) >> 4;
        if (sectionIndex < 0 || sectionIndex >= sectionCount) {
            return;
        }
        long chunkKey = this.getChunkKey(x >> 4, z >> 4);
        BaseChunk[] sections = this.chunkData.computeIfAbsent(chunkKey, ignored -> new BaseChunk[sectionCount]);
        BaseChunk section = sections[sectionIndex];
        if (section == null) {
            sections[sectionIndex] = section = BaseChunk.create();
        }
        WrappedBlockState previousState = section.get(this.playerData.getClientVersion(), x & 0xF, (y - minY) & 0xF, z & 0xF, false);
        section.set(x & 0xF, (y - minY) & 0xF, z & 0xF, blockState.getGlobalId());
        this.updateSulfurTracking(x, y, z, previousState, blockState);
    }

    private void applyChunkColumn(int chunkX, int chunkZ, BaseChunk[] sections, TileEntity[] tileEntities) {
        if (ExemptionManager.isAtLeast26_1()) {
            this.clearChunkSections(chunkX, chunkZ, sections.length);
            if (tileEntities != null && tileEntities.length != 0) {
                int minY = this.getMinY();
                for (TileEntity tileEntity : tileEntities) {
                    int x = (chunkX << 4) + tileEntity.getX();
                    int y = tileEntity.getY();
                    int z = (chunkZ << 4) + tileEntity.getZ();
                    int sectionIndex = (y - minY) >> 4;
                    if (sectionIndex >= 0 && sectionIndex < sections.length) {
                        BaseChunk section = sections[sectionIndex];
                        if (section != null) {
                            WrappedBlockState blockState = section.get(this.playerData.getClientVersion(), x & 15, (y - minY) & 15, z & 15, false);
                            if (this.isWetPotentSulfur(blockState)) {
                                this.trackBlockPosition(x, y, z);
                            }
                        }
                    }
                }
            }
        }
    }

    private void updateSulfurTracking(int x, int y, int z, WrappedBlockState previousState, WrappedBlockState blockState) {
        if (ExemptionManager.isAtLeast26_1()) {
            boolean wasWetSulfur = this.isWetPotentSulfur(previousState);
            boolean isWetSulfur = this.isWetPotentSulfur(blockState);
            if (wasWetSulfur && !isWetSulfur) {
                this.untrackBlockPosition(x, y, z);
            } else if (!wasWetSulfur && isWetSulfur) {
                this.trackBlockPosition(x, y, z);
            }
        }
    }

    private boolean isWetPotentSulfur(WrappedBlockState blockState) {
        return blockState != null && blockState.getType() == StateTypes.POTENT_SULFUR && blockState.getPotentSulfurState() != PotentSulfurState.DRY;
    }

    private void trackBlockPosition(int x, int y, int z) {
        int sectionIndex = (y - this.getMinY()) >> 4;
        long sectionKey = this.getBlockKey(x >> 4, z >> 4, sectionIndex);
        Map<Long, BlockPosition> positions = this.trackedBlocks.get(sectionKey);
        if (positions == null) {
            positions = new HashMap<>();
            this.trackedBlocks.put(sectionKey, positions);
        }
        long positionKey = this.getPositionKey(x, y, z);
        if (!positions.containsKey(positionKey)) {
            positions.put(positionKey, new BlockPosition(x, y, z));
        }
    }

    private void untrackBlockPosition(int x, int y, int z) {
        int sectionIndex = (y - this.getMinY()) >> 4;
        long sectionKey = this.getBlockKey(x >> 4, z >> 4, sectionIndex);
        Map<Long, BlockPosition> positions = this.trackedBlocks.get(sectionKey);
        if (positions != null) {
            positions.remove(this.getPositionKey(x, y, z));
            if (positions.isEmpty()) {
                this.trackedBlocks.remove(sectionKey);
            }
        }
    }

    private void clearChunkSections(int chunkX, int chunkZ, int sectionCount) {
        for (byte sectionIndex = 0; sectionIndex < sectionCount; sectionIndex += 1) {
            this.trackedBlocks.remove(this.getBlockKey(chunkX, chunkZ, sectionIndex));
        }
    }

    public boolean hasTrackedBlockNearby(double x, double y, double z) {
        if (!ExemptionManager.isAtLeast26_1()) {
            return false;
        }
        int minX = this.floorInt(x - 3.5);
        int maxX = this.floorInt(x + 3.5);
        int minY = Math.max(this.getMinY(), this.floorInt(y - 20.0));
        int maxY = Math.min(this.getMaxY(), this.floorInt(y));
        int minZ = this.floorInt(z - 3.5);
        int maxZ = this.floorInt(z + 3.5);
        int minChunkX = minX >> 4;
        int maxChunkX = maxX >> 4;
        int minChunkZ = minZ >> 4;
        int maxChunkZ = maxZ >> 4;
        int minSection = (minY - this.getMinY()) >> 4;
        int maxSection = (maxY - this.getMinY()) >> 4;
        for (int chunkX = minChunkX; chunkX <= maxChunkX; ++chunkX) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; ++chunkZ) {
                if (!this.chunkData.containsKey(this.getChunkKey(chunkX, chunkZ))) {
                    continue;
                }
                for (int sectionIndex = minSection; sectionIndex <= maxSection; ++sectionIndex) {
                    Map<Long, BlockPosition> positions = this.trackedBlocks.get(this.getBlockKey(chunkX, chunkZ, sectionIndex));
                    if (positions == null) {
                        continue;
                    }
                    for (BlockPosition position : positions.values()) {
                        double deltaX = x - (position.getX() + 0.5);
                        double deltaZ = z - (position.getZ() + 0.5);
                        double deltaY = y - (position.getY() + 0.5);
                        if (deltaX * deltaX + deltaZ * deltaZ < 12.25 && deltaY >= 0.0 && deltaY < 20.0) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public WrappedBlockState getBlockState(Vector3i position) {
        return this.getBlockState(position.getX(), position.getY(), position.getZ());
    }

    public WrappedBlockState getBlockState(int x, int y, int z) {
        BaseChunk[] sections = this.chunkData.get(this.getChunkKey(x >> 4, z >> 4));
        if (sections == null) {
            return WrappedBlockState.getByGlobalId(0);
        }
        int minY = this.getMinY();
        if (y >= minY && y <= this.getMaxY()) {
            int sectionIndex = (y - minY) >> 4;
            if (sectionIndex >= 0 && sectionIndex < sections.length && sections[sectionIndex] != null) {
                BaseChunk section = sections[sectionIndex];
                return section.get(this.playerData.getClientVersion(), x & 15, (y - minY) & 15, z & 15, false);
            }
        }
        return WrappedBlockState.getByGlobalId(0);
    }

    public void reset() {
        this.chunkData.clear();
        this.trackedBlocks.clear();
    }

    private int getMinY() {
        return this.playerData.getClientVersion().getProtocolVersion() >= 757 ? -64 : 0;
    }

    private int getMaxY() {
        return this.playerData.getClientVersion().getProtocolVersion() >= 757 ? 319 : 255;
    }

    private int getSectionCount() {
        return (this.getMaxY() - this.getMinY() + 1) / 16;
    }

    private long getChunkKey(int chunkX, int chunkZ) {
        return (chunkX & 4294967295L) << 32 | chunkZ & 4294967295L;
    }

    private long getBlockKey(int chunkX, int chunkZ, int sectionIndex) {
        return (chunkX & 67108863L) << 38 | (chunkZ & 67108863L) << 12 | sectionIndex & 4095L;
    }

    private long getPositionKey(int x, int y, int z) {
        return (x & 67108863L) << 38 | (z & 67108863L) << 12 | y & 4095L;
    }

    private int floorInt(double value) {
        int truncated = (int) value;
        return value < truncated ? truncated - 1 : truncated;
    }

    public Vector3i getPosition(Vector3i position, BlockFace face) {
        switch(face) {
            case UP:
                return new Vector3i(position.getX(), position.getY() + 1, position.getZ());
            case DOWN:
                return new Vector3i(position.getX(), position.getY() - 1, position.getZ());
            case NORTH:
                return new Vector3i(position.getX(), position.getY(), position.getZ() - 1);
            case SOUTH:
                return new Vector3i(position.getX(), position.getY(), position.getZ() + 1);
            case WEST:
                return new Vector3i(position.getX() - 1, position.getY(), position.getZ());
            case EAST:
                return new Vector3i(position.getX() + 1, position.getY(), position.getZ());
            default:
                return position;
        }
    }

    private void applyBlockChanges(EncodedBlock[] blocks) {
        for (EncodedBlock block : blocks) {
            WrappedBlockState blockState = block.getBlockState(this.playerData.getClientVersion());
            this.cacheBlockChange(block.getX(), block.getY(), block.getZ(), blockState);
        }
    }

    private void applyBlockChange(Vector3i position, WrappedBlockState blockState) {
        this.cacheBlockChange(position.getX(), position.getY(), position.getZ(), blockState);
    }

    private void removeChunk(int chunkX, int chunkZ) {
        BaseChunk[] removedSections = chunkData.remove(getChunkKey(chunkX, chunkZ));
        clearChunkSections(chunkX, chunkZ, removedSections == null ? getSectionCount() : removedSections.length);
    }

    private void applyBulkChunkData(int[] chunkXs, int[] chunkZs, BaseChunk[][] chunks) {
        int chunkCount = Math.min(Math.min(chunkXs.length, chunkZs.length), chunks.length);
        for (int index = 0; index < chunkCount; index++) {
            chunkData.put(getChunkKey(chunkXs[index], chunkZs[index]), chunks[index]);
        }
    }

    private void applyChunkData(int chunkX, int chunkZ, boolean fullChunk, BaseChunk[] sections, TileEntity[] tileEntities) {
        long chunkKey = getChunkKey(chunkX, chunkZ);
        if (fullChunk) {
            chunkData.put(chunkKey, sections);
            applyChunkColumn(chunkX, chunkZ, sections, tileEntities);
        } else {
            BaseChunk[] existingSections = chunkData.computeIfAbsent(chunkKey, ignored -> new BaseChunk[sections.length]);
            for (int index = 0; index < Math.min(sections.length, existingSections.length); index++) {
                if (sections[index] != null) {
                    existingSections[index] = sections[index];
                }
            }
        }
    }
}
