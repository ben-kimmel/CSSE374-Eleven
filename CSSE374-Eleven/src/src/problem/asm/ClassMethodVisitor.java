package src.problem.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import src.problem.components.IClass;
import src.problem.components.IMethod;
import src.problem.components.IParameter;
import src.problem.components.IRelation;
import src.problem.components.Method;
import src.problem.components.Parameter;
import src.problem.components.Relation;
import src.problem.components.Relation.RelationType;

public class ClassMethodVisitor extends ClassVisitor {
	
	private IClass clazz;
	
	public ClassMethodVisitor(int api, IClass clazz) {
		super(api);
		this.clazz = clazz;
	}

	public ClassMethodVisitor(int api, ClassVisitor decorated, IClass clazz) {
		super(api, decorated);
		this.clazz = clazz;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		MethodVisitor toDecorate = super.visitMethod(access, name, desc, signature, exceptions);
		MethodBodyVisitor mbv = new MethodBodyVisitor(this.api, toDecorate, this.clazz);
		//System.out.println("method " + name);
		// DONE: create an internal representation of the current method and
		// pass it to the methods below
		IMethod method = new Method();
		method.setName(name);
		
		addAccessLevel(access, method);
		addReturnType(desc, method);
		addArguments(desc, method);
		// DONE: add the current method to your internal representation of the
		// current class
		this.clazz.addMethod(method);
		
		return mbv;
	}

	private void addAccessLevel(int access, IMethod method) {
		String level = "";
		if ((access & Opcodes.ACC_PUBLIC) != 0) {
			level = "public";
		} else if ((access & Opcodes.ACC_PROTECTED) != 0) {
			level = "protected";
		} else if ((access & Opcodes.ACC_PRIVATE) != 0) {
			level = "private";
		} else {
			level = "default";
		}
		
		//System.out.println("access level: " + level);
		//DONE: ADD this information to your representation of the current
		// method.
		method.addModifier(level);
		method.setVisibility(level);
	}

	private void addReturnType(String desc, IMethod method) {
		String returnType = Type.getReturnType(desc).getClassName();
		//System.out.println("return type: " + returnType);
		// DONE: ADD this information to your representation of the current
		// method.
		returnType = simplifyClassName(returnType);
		method.setReturnType(returnType);
		IRelation relation = new Relation(this.clazz.getName(), returnType, RelationType.USES);
		this.clazz.addRelation(relation);
	}

	private void addArguments(String desc, IMethod method) {
		Type[] args = Type.getArgumentTypes(desc);
		for (int i = 0; i < args.length; i++) {
			String arg = args[i].getClassName();
			//System.out.println("arg " + i + ": " + arg);
			// DONE: ADD this information to your representation of the current
			// method.
			arg = simplifyClassName(arg);
			IParameter parameter = new Parameter();
			parameter.setType(arg);
			IRelation relation = new Relation(this.clazz.getName(), arg, RelationType.USES);
			this.clazz.addRelation(relation);
			method.addParameter(parameter);
		}
	}
	
	private String simplifyClassName(String arg) {
		if(arg.contains(".")) {
			String[] splitType = arg.split("\\.");
			arg = splitType[splitType.length - 1];
		}
		return arg;
	}
}