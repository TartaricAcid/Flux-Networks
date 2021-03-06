package sonar.flux.api;

import net.minecraft.world.World;
import sonar.core.api.utils.BlockCoords;
import sonar.flux.connection.EmptyFluxNetwork;

public class ClientFlux implements IFlux {

	public BlockCoords coords;	
	public ConnectionType type;	
	public int priority; 
	public long limit;
	public String customName;
	
	public ClientFlux(IFlux flux){
		this.coords=flux.getCoords();
		this.type=flux.getConnectionType();
		this.priority=flux.getCurrentPriority();
		this.limit=flux.getTransferLimit();
		this.customName=flux.getCustomName();
	}
	
	public ClientFlux(BlockCoords coords, ConnectionType type, int priority, long limit, String customName){
		this.coords=coords;
		this.type=type;
		this.priority=priority;
		this.limit=limit;
		this.customName=customName;
	}
	
	@Override
	public World getDimension() {
		return coords.getWorld();
	}

	@Override
	public BlockCoords getCoords() {
		return coords;
	}

	@Override
	public IFluxNetwork getNetwork() {
		return EmptyFluxNetwork.INSTANCE;
	}

	@Override
	public ConnectionType getConnectionType() {
		return type;
	}

	@Override
	public long getTransferLimit() {
		return limit;
	}

	@Override
	public int getCurrentPriority() {
		return priority;
	}

	@Override
	public String getCustomName() {
		return customName;
	}

}
