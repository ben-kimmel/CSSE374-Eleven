package src.problem.outputvisitor;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import src.problem.components.IClass;

/*import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;*/

import src.problem.components.IField;
import src.problem.components.IMethod;
import src.problem.components.IModel;
import src.problem.components.IParameter;
import src.problem.components.IRelation;

public class GraphVizOutputStream extends FilterOutputStream {

	private final IVisitor visitor;
	private StringBuilder output;

	public GraphVizOutputStream(OutputStream out) {
		super(out);
		this.visitor = new Visitor();
		this.output = new StringBuilder();
		this.setupVisitors();
	}

	private void setupVisitors() {
		this.setupPreVisitModel();
		this.setupPostVisitModel();
		this.setupPreVisitClass();
		this.setupVisitClass();
		this.setupPostVisitClass();
		this.setupVisitField();
		this.setupPreVisitMethod();
		this.setupPostVisitMethod();
		this.setupVisitParameter();
		this.setupVisitRelationship();
	}
	
	private void write(String m) {
		try {
			super.write(m.getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String write(IModel m) {
		ITraverser t = (ITraverser) m;
		t.accept(this.visitor);
		System.out.println(this.output.toString());
		return this.output.toString();
	}

	public void setupPreVisitModel() {
		this.visitor.addVisit(VisitType.PreVisit, IModel.class, (ITraverser t) -> {
			this.write("digraph G {fontname = \"Bitstream Vera Sans\" fontsize = 8\nnode [fontname ="
					+ "\"Bitstream Vera Sans\" fontsize = 8 shape = \"record\"] edge [fontname = "
					+ "\"Bitstream Vera Sans\" fontsize = 8]");
		});
	}

	public void setupPostVisitModel() {
		this.visitor.addVisit(VisitType.PostVisit, IModel.class, (ITraverser t) -> {
			this.write("\n}");
		});

	}

	private void setupPreVisitClass() {
		this.visitor.addVisit(VisitType.PreVisit, IClass.class, (ITraverser t) -> {
			IClass c = (IClass) t;
			StringBuilder ret = new StringBuilder();
			ret.append(c.getName());
			ret.append(" [label = \"{");
			if (c.getIsInterface()) {
				ret.append("\\<\\<interface\\>\\>");
				ret.append("\\n");
			}
			ret.append(c.getName());
			ret.append("|");

			this.write(ret.toString());
		});

	}

	private void setupVisitClass() {
		this.visitor.addVisit(VisitType.Visit, IClass.class, (ITraverser t) -> {
			this.write("|");
		});

	}

	private void setupPostVisitClass() {
		this.visitor.addVisit(VisitType.PostVisit, IClass.class, (ITraverser t) -> {
			this.write("}\"]");
		});

	}

	private void setupPreVisitMethod() {
		this.visitor.addVisit(VisitType.PreVisit, IMethod.class, (ITraverser t) -> {
			IMethod c = (IMethod) t;
			StringBuilder ret = new StringBuilder();

			if (c.getVisibility().equals("public")) {
				ret.append("+ ");
			} else if (c.getVisibility().equals("private")) {
				ret.append("- ");
			} else if (c.getVisibility().equals("protected")) {
				ret.append("# ");
			}

			ret.append(c.getName());
			ret.append("(");
			this.write(ret.toString());
		});
	}

	private void setupPostVisitMethod() {
		this.visitor.addVisit(VisitType.PostVisit, IMethod.class, (ITraverser t) -> {
			IMethod c = (IMethod) t;
			this.write(") : ");
			this.write(c.getReturnType());
			this.write("\\l");
		});

	}

	private void setupVisitField() {
		this.visitor.addVisit(VisitType.Visit, IField.class, (ITraverser t) -> {
			IField c = (IField) t;
			StringBuilder ret = new StringBuilder();

			if (c.getVisibility().equals("public")) {
				ret.append("+ ");
			} else if (c.getVisibility().equals("private")) {
				ret.append("- ");
			} else if (c.getVisibility().equals("protected")) {
				ret.append("# ");
			}

			ret.append(c.getName());
			ret.append(" : ");
			ret.append(c.getType());
			ret.append("\\l");

			this.write(ret.toString());
		});

	}

	private void setupVisitRelationship() {
		this.visitor.addVisit(VisitType.Visit, IRelation.class, (ITraverser t) -> {
			IRelation c = (IRelation) t;
			switch (c.getType()) {
				case EXTENDS:
					this.write("edge [ arrowhead = \"onormal\" style = \"solid\" ]\n" + c.getSrc() + " -$ " + c.getDest()
							+ "\n");
					break;
				case IMPLEMENTS:
					this.write("edge [ arrowhead = \"onormal\" style = \"dashed\" ]\n" + c.getSrc() + " -$ " + c.getDest()
							+ "\n");
					break;
				case ASSOCIATION:
					this.write(
							"edge [ arrowhead = \"vee\" style = \"solid\" ]\n" + c.getSrc() + " -$ " + c.getDest() + "\n");
					break;
				case USES:
					this.write(
							"edge [ arrowhead = \"vee\" style = \"dashed\" ]\n" + c.getSrc() + " -$ " + c.getDest() + "\n");
					break;
			}
		});
	}

	private void setupVisitParameter() {
		this.visitor.addVisit(VisitType.Visit, IParameter.class, (ITraverser t) -> {
			IParameter c = (IParameter) t;
			this.write(c.getType());
		});
	}

}
