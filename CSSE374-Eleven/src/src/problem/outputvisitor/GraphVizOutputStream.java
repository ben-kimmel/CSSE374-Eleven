package src.problem.outputvisitor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import src.problem.components.Class;
import src.problem.components.Field;
import src.problem.components.IModel;
import src.problem.components.Method;
import src.problem.components.Model;
import src.problem.components.Parameter;
import src.problem.components.PatternType;
import src.problem.components.Relation;

public class GraphVizOutputStream extends FilterOutputStream {

	private final IVisitor visitor;
	private Map<String, String> relationTypeOutput;


	public GraphVizOutputStream(OutputStream out) {
		super(out);
		this.visitor = new Visitor();
		this.setupVisitors();
		relationTypeOutput = new HashMap<String, String>();
		try {
			loadRelationTypeOutput();
		} catch (IOException e) {
			System.out.println("Error loading relation type configuration.");
		}
	}

	private void loadRelationTypeOutput() throws IOException {
		BufferedReader in = new BufferedReader(new FileReader("relationTypesConfig.txt"));
        String line = "";
        while ((line = in.readLine()) != null) {
        	String[] current = line.split("-");
        	relationTypeOutput.put(current[0], current[1] + "-" + current[2]);
        }
        in.close();
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
	
	public void write(IModel m) {
		ITraverser t = (ITraverser) m;
		t.accept(this.visitor);
	}

	private void setupPreVisitModel() {
		this.visitor.addVisit(VisitType.PreVisit, Model.class, (ITraverser t) -> {
			this.write("digraph G {fontname = \"Bitstream Vera Sans\" fontsize = 8\nnode [fontname ="
					+ "\"Bitstream Vera Sans\" fontsize = 8 shape = \"record\"] edge [fontname = "
					+ "\"Bitstream Vera Sans\" fontsize = 8]");
		});
	}

	private void setupPostVisitModel() {
		this.visitor.addVisit(VisitType.PostVisit, Model.class, (ITraverser t) -> {
			this.write("\n}");
		});

	}

	private void setupPreVisitClass() {
		this.visitor.addVisit(VisitType.PreVisit, Class.class, (ITraverser t) -> {
			Class c = (Class) t;
			StringBuilder ret = new StringBuilder();
			ret.append(c.getName());
			ret.append("[");
			ret.append(this.getColor(c.getPattern()));
			ret.append(" label = \"{");
			if (c.getIsInterface()) {
				ret.append("\\<\\<interface\\>\\>");
				ret.append("\\n");
			}
			ret.append(c.getName());
			ret.append(this.getStereotype(c.getStereotype()));
			ret.append("|");

			this.write(ret.toString());
		});

	}

	private Object getColor(PatternType pattern) {
		switch(pattern) {
		case SINGLETON:
			return "style=filled fillcolor=blue";
		case DECORATOR:
			return "style=filled fillcolor=green";
		case ADAPTER:
			return "style=filled fillcolor=red";
		case COMPOSITE:
			return "style=filled fillcolor=yellow";
		default:
			return "";
		}
	}

	private String getStereotype(String string) {
		if(string == null) {
			return "";
		} 
		return "\\n\\<\\<" + string + "\\>\\>";
	}

	private void setupVisitClass() {
		this.visitor.addVisit(VisitType.Visit, Class.class, (ITraverser t) -> {
			this.write("|");
		});

	}

	private void setupPostVisitClass() {
		this.visitor.addVisit(VisitType.PostVisit, Class.class, (ITraverser t) -> {
			this.write("}\"]\n");
		});

	}

	private void setupVisitField() {
		this.visitor.addVisit(VisitType.Visit, Field.class, (ITraverser t) -> {
			Field c = (Field) t;
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
			ret.append("\\l\n");
			String mod = ret.toString().replace("<", "\\<");
			mod = mod.replace(">", "\\>");
			this.write(mod);
		});
	
	}

	private void setupPreVisitMethod() {
		this.visitor.addVisit(VisitType.PreVisit, Method.class, (ITraverser t) -> {
			Method c = (Method) t;
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
			String mod = ret.toString().replace("<", "\\<");
			mod = mod.replace(">", "\\>");
			this.write(mod);
		});
	}

	private void setupPostVisitMethod() {
		this.visitor.addVisit(VisitType.PostVisit, Method.class, (ITraverser t) -> {
			Method c = (Method) t;
			StringBuilder ret = new StringBuilder();
			ret.append(") : ");
			ret.append(c.getReturnType());
			ret.append("\\l\n");
			
			String mod = ret.toString().replace("<", "\\<");
			mod = mod.replace(">", "\\>");
			this.write(mod);
		});

	}

	private void setupVisitRelationship() {
		this.visitor.addVisit(VisitType.Visit, Relation.class, (ITraverser t) -> {
			Relation c = (Relation) t;
			String style, arrowhead, label;
			String type = c.getType();
			if(relationTypeOutput.containsKey(type)) {
				String[] output = relationTypeOutput.get(type).split("-");
				arrowhead = output[0];
				style = output[1];
			} else {
				arrowhead = "";
				style = "";
			}
			if (c.getLabel() == null) {
				label = " label=\"\"" ;
			} else {
				label = " label=\"" + c.getLabel() + "\" ";
			}
			this.write("edge [ arrowhead = " + arrowhead + " style = " + style + " " + label + "]\n" + c.getSrc() + " -> " + c.getDest() + "\n");
		});
	}

	private void setupVisitParameter() {
		this.visitor.addVisit(VisitType.Visit, Parameter.class, (ITraverser t) -> {
			Parameter c = (Parameter) t;
			String mod = c.getType().toString().replace("<", "\\<");
			mod = mod.replace(">", "\\>");
			this.write(mod + " ");
		});
	}

}
