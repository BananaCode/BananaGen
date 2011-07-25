package net.llamaslayers.minecraft.banana.gen;

/**
 * Used for fast storage, comparison, and recall of block positions. Mutable
 * to avoid creating new objects for simple comparison.
 *
 * @author Nightgunner5
 */
public class XYZ {
	public int x;
	public int y;
	public int z;

	public XYZ(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public XYZ() {
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof XYZ)) {
			return false;
		}
		XYZ other = (XYZ) obj;
		if (x != other.x) {
			return false;
		}
		if (y != other.y) {
			return false;
		}
		if (z != other.z) {
			return false;
		}
		return true;
	}
}