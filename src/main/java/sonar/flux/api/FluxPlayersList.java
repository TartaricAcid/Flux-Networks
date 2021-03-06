package sonar.flux.api;

import java.util.ArrayList;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.IDirtyPart;

public class FluxPlayersList extends ArrayList<FluxPlayer> implements IDirtyPart, INBTSyncable {

	public boolean hasChanged = true;
	
	public boolean containsUUID(UUID check) {
		for (int i = 0; i < size(); i++) {
			FluxPlayer player = this.get(i);
			if (player != null && player.id.equals(check)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean containsPlayer(FluxPlayer check) {
		for (int i = 0; i < size(); i++) {
			FluxPlayer player = this.get(i);
			if (player != null && player.equals(check)) {
				return true;
			}
		}
		return false;
	}

	public boolean add(FluxPlayer player) {
		if (!containsPlayer(player)) {
			setChanged(super.add(player));
			return hasChanged;
		}
		return false;
	}

	public boolean remove(FluxPlayer player) {
		setChanged(super.remove(player));
		return hasChanged;
	}

	@Override
	public boolean hasChanged() {
		return hasChanged;
	}

	@Override
	public void setChanged(boolean set) {
		hasChanged = set;
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		if (nbt.hasKey("playerList")) {
			NBTTagList list = nbt.getTagList("playerList", 10);
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound tag = list.getCompoundTagAt(i);
				add(new FluxPlayer(tag));
			}
		}
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		if (this.hasChanged || type.mustSync()) {
			NBTTagList list = new NBTTagList();
			forEach(player -> list.appendTag(player.writeData(new NBTTagCompound(), type)));
			nbt.setTag("playerList", list);
		}
		return nbt;
	}
}
