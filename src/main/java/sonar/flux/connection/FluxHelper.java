package sonar.flux.connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import sonar.core.api.SonarAPI;
import sonar.core.api.utils.ActionType;
import sonar.core.api.utils.BlockCoords;
import sonar.flux.FluxNetworks;
import sonar.flux.api.FluxWrapper;
import sonar.flux.api.IFlux;
import sonar.flux.api.IFluxCommon;
import sonar.flux.common.tileentity.TileEntityFlux;

public class FluxHelper extends FluxWrapper {

	public ArrayList<? extends IFluxCommon> getAllNetworks() {
		return FluxNetworks.cache.getAllNetworks();
	}

	public ArrayList<? extends IFluxCommon> getAvailableNetworks(String playerName, boolean admin) {
		return FluxNetworks.cache.getAllowedNetworks(playerName, admin);
	}

	public long pullEnergy(IFlux from, long maxTransferRF, ActionType actionType) {
		long extracted = 0;
		maxTransferRF = Math.min(maxTransferRF, from.getTransferLimit());
		if (from != null && maxTransferRF != 0) {
			BlockCoords coords = from.getCoords();
			switch (from.getConnectionType()) {
			case PLUG:
				for (EnumFacing face : EnumFacing.VALUES) {
					BlockCoords translate = BlockCoords.translateCoords(coords, face);
					TileEntity tile = translate.getTileEntity();
					if (tile != null && !(tile instanceof IFlux)) {
						extracted += SonarAPI.getEnergyHelper().extractEnergy(tile, Math.min(maxTransferRF, from.getTransferLimit()), face, actionType);
					}
				}
				break;
			case STORAGE:
				extracted += ((TileEntityFlux.Storage) coords.getTileEntity()).storage.extractEnergy((int) Math.min(maxTransferRF, Integer.MAX_VALUE), actionType.shouldSimulate());
				break;
			default:
				break;
			}
		}
		return extracted;
	}

	public long pushEnergy(IFlux to, long maxTransferRF, ActionType actionType) {
		long received = 0;
		maxTransferRF = Math.min(maxTransferRF, to.getTransferLimit());
		if (to != null && maxTransferRF != 0) {
			BlockCoords coords = to.getCoords();
			switch (to.getConnectionType()) {
			case POINT:
				for (EnumFacing face : EnumFacing.VALUES) {
					BlockCoords translate = BlockCoords.translateCoords(coords, face);
					TileEntity tile = translate.getTileEntity();
					if (tile != null && !(tile instanceof IFlux)) {
						received += SonarAPI.getEnergyHelper().receiveEnergy(tile, Math.min(maxTransferRF, to.getTransferLimit()), face, actionType);
					}
				}
				break;
			case STORAGE:
				TileEntity tile = coords.getTileEntity();
				if (tile != null && tile instanceof TileEntityFlux.Storage)
					received += ((TileEntityFlux.Storage) coords.getTileEntity()).storage.receiveEnergy((int) Math.min(maxTransferRF, Integer.MAX_VALUE), actionType.shouldSimulate());
				break;
			case CONTROLLER:
				break;
			default:
				break;
			}
		}
		return received;
	}

	/** returns amount used */
	/*
	 * public long pushEnergy(IFlux from, long maxTransferRF, ActionType actionType) { //long transferred = maxTransferRF; long used = 0; long max = maxTransferRF; if (from != null && maxTransferRF != 0) { BlockCoords coords = from.getCoords(); for (EnumFacing face : EnumFacing.VALUES) { BlockCoords translate = BlockCoords.translateCoords(coords, face); TileEntity tile = translate.getTileEntity(); if (tile != null && !(tile instanceof IFlux)) { //long maxReceive = SonarAPI.getEnergyHelper().receiveEnergy(tile, maxTransferRF-used, face, ActionType.SIMULATE); //if (maxReceive != 0) { //System.out.print(arg0); long before = maxTransferRF-used; //System.out.print(before); long transfer=before-SonarAPI.getEnergyHelper().receiveEnergy(tile, maxTransferRF-used, face, actionType); //System.out.print(transfer); used += transfer; //} } } } System.out.print(used);
	 * 
	 * return used; }
	 */

	/** gets all the TileEntities which can send/receive energy adjacent to the given IFlux */
	public Map<TileEntity, EnumFacing> getConnections(IFlux flux) {
		Map<TileEntity, EnumFacing> tiles = new HashMap();
		for (EnumFacing face : EnumFacing.VALUES) {
			World world = flux.getDimension();
			TileEntity tile = world.getTileEntity(flux.getCoords().getBlockPos().offset(face));
			if (tile == null || tile.isInvalid()) {
				continue;
			}
			if (SonarAPI.getEnergyHelper().canTransferEnergy(tile, face) != null) {
				tiles.put(tile, face);
			}
		}
		return tiles;
	}
}