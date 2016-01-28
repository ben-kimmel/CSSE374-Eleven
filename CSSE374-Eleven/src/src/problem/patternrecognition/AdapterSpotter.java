package src.problem.patternrecognition;

import java.util.HashSet;
import java.util.Set;

import src.problem.components.*;

public class AdapterSpotter implements IMultipleClassPatternSpotter {

	private IModel m;

	@Override
	public void spot(IModel m) {
		this.m = m;
		Set<IRelation> relations = m.getRelations();
		Set<IRelation> filtered = new HashSet<IRelation>();
		Set<String> impclasses = new HashSet<String>();
		Set<String> assocclasses = new HashSet<String>();
		for (IRelation r : relations) {
			//System.out.println(r.getDest());
			if (r.getType() == RelationType.IMPLEMENTS) {
				filtered.add(r);
				impclasses.add(r.getSrc());
			}
			if (r.getType() == RelationType.ASSOCIATION) {
				filtered.add(r);
				assocclasses.add(r.getSrc());
			}
			if (r.getType() == RelationType.USES) {
				filtered.add(r);
				impclasses.add(r.getSrc());
			}
		}
		for (String s : impclasses) {
			if (assocclasses.contains(s)) {
				checkForAdapter(s, filtered);
			}
		}
	}

	private boolean checkForAdapter(String s, Set<IRelation> filtered) {
		IClass adapter = findClass(s);
		IClass adaptee = null;
		IClass target = null;
		System.out.println("checking " + adapter.getName() + " for adaptors");
		for (IRelation r : filtered) {
			//System.out.println("checking relation " + r.getSrc() + "->" + r.getDest() + " (" + r.getType() + ")");
			if (r.getSrc().equals(s) && r.getType() == RelationType.ASSOCIATION) {
				System.out.println("FOUND ADAPTEE!!! Relation " + r.getSrc() + "->" + r.getDest() + " (" + r.getType() + ")");
				adaptee = findClass(r.getDest());
				if (adaptee != null) {
					System.out.println("found adaptee: " + adaptee.getName());
				}
			}
//			System.out.println("Relation " + r.getSrc() + "->" + r.getDest() + " (" + r.getType() + ") results: "
//					+ r.getDest().equals(s) + ", " + (r.getType() == RelationType.IMPLEMENTS) + ", " + (r.getType() == RelationType.USES));
			if (r.getSrc().equals(s) && r.getType() == RelationType.IMPLEMENTS) {
				//System.out.println("FOUND TARGET!!! Relation " + r.getSrc() + "->" + r.getDest() + " (" + r.getType() + ")");
				target = findClass(r.getDest());
				if (target != null) {
					System.out.println("found target: " + target.getName());
				}

			}
		}
		if (adaptee == null || target == null)
			return false;
		System.out.println("MADE IT");
		for (IField f : adapter.getFields()) {
			System.out.println("field " + f.getType() + " adaptee type " + adaptee.getName());
			if (f.getType().contains(adaptee.getName())) {
				// set the appropriate Stereotype and PatternType fields
				adapter.setStereotype("adapter");
				adaptee.setStereotype("adaptee");
				target.setStereotype("target");
				adapter.setPattern(PatternType.ADAPTER);
				adaptee.setPattern(PatternType.ADAPTER);
				target.setPattern(PatternType.ADAPTER);
				System.out.println("adapter alert");
				return true;
			}
		}

		return false;
	}

	private IClass findClass(String s) {
		for (IClass c : m.getClasses()) {
			if (c.getName().equals(s)) {
				return c;
			}
		}
		return null;
	}
}