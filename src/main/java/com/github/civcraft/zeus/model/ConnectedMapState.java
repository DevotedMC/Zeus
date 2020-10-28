package com.github.civcraft.zeus.model;

import com.google.common.base.Preconditions;

public class ConnectedMapState {
	
	private ZeusLocation upperLeftCorner;
	private int xSize;
	private int zSize;
	
	public ConnectedMapState(ZeusLocation upperLeftCorner, int xSize, int zSize) {
		Preconditions.checkNotNull(upperLeftCorner);
		Preconditions.checkArgument(xSize > 0);
		Preconditions.checkArgument(zSize > 0);
		this.upperLeftCorner = upperLeftCorner;
		this.xSize = xSize;
		this.zSize = zSize;
	}
	
	public ZeusLocation getUpperLeftCorner() {
		return upperLeftCorner;
	}
	
	public String getWorld() {
		return upperLeftCorner.getWorld();
	}
	
	public int getXSize() {
		return xSize;
	}
	
	public int getZSize() {
		return zSize;
	}
	
	public boolean isInside(ZeusLocation location) {
		return isInside(location.getX(), location.getZ());
	}
	
	public boolean isInside(double x, double z) {
		double xDiff = x - upperLeftCorner.getX();
		if (xDiff < 0 || xDiff > xSize) {
			return false;
		}
		double zDiff = z - upperLeftCorner.getZ();
		if (zDiff < 0 || zDiff > zSize) {
			return false;
		}
		return true;
	}

}
