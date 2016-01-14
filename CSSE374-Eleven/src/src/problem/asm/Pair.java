package src.problem.asm;

public class Pair<C, M> {

	private C left;
	private M right;

	public C getLeft() {
		return left;
	}

	public void setLeft(C left) {
		this.left = left;
	}

	public M getRight() {
		return right;
	}

	public void setRight(M right) {
		this.right = right;
	}

	public Pair(C left, M right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair other = (Pair) obj;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
	}

}
