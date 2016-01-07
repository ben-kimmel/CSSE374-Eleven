package src.problem.asm;

import java.io.IOException;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import src.problem.components.*;
import src.problem.components.Class;

public class DesignParser {
	/**
	 * Reads in a list of Java Classes and reverse engineers their design.
	 *
	 * @param args:
	 *            the names of the classes, separated by spaces. For example:
	 *            java DesignParser java.lang.String
	 *            edu.rosehulman.csse374.ClassFieldVisitor java.lang.Math
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		AllClasses allClasses = new AllClasses();

		// Used to generate UML for Lab 1-3 code
		String[] m1 = new String[] { "lab1_3.AppLauncher", "lab1_3.EntryDeleteObserver", "lab1_3.EntryModifyObserver",
				"lab1_3.HtmlCreateObserver", "lab1_3.Observer", "lab1_3.PngCreateObserver", "lab1_3.Subject",
				"lab1_3.TxtCreateObserver" };
		
		String[] m2 = new String[] { "lab2_3.Cheese", "lab2_3.ChicagoPizzaIngredientFactory", "lab2_3.Clams", "lab2_3.Dough",
				"lab2_3.FreshClams", "lab2_3.FrozenClams", "lab2_3.MarinaraSauce", "lab2_3.MozzarellaCheese", "lab2_3.NYPizzaIngredientFactory",
				"lab2_3.NYPizzaStore", "lab2_3.PizzaIngredientFactory", "lab2_3.PlumTomatoSauce", "lab2_3.ReggianoCheese", "lab2_3.Sauce",
				"lab2_3.ThickCrustDough", "lab2_3.ThinCrustDough" };
		
		String [] m3 = new String[] {"src.problem.components.AllClasses", "src.problem.components.Class", 
				"src.problem.components.IClass", "src.problem.components.Field",  "src.problem.components.IField", 
				"src.problem.components.Method", "src.problem.components.IMethod", "src.problem.components.Parameter", 
				"src.problem.components.IParameter", "src.problem.components.IGraphVizComponent"};
		
		//args = new String[]{"tests.TestClass", "tests.TestClassTwo"};
		for (String className : m2) {
			IClass clazz = parse(className);
			allClasses.addClass(clazz);
		}

		System.out.println(allClasses.getGraphViz());
	}

	public static IClass parse(String args) throws IOException {
		IClass clazz = new Class();

		// ASM's ClassReader does the heavy lifting of parsing the compiled
		// Java class
		ClassReader reader = new ClassReader(args);
		// make class declaration visitor to get superclass and interfaces
		ClassVisitor decVisitor = new ClassDeclarationVisitor(Opcodes.ASM5, clazz);
		// DECORATE declaration visitor with field visitor
		ClassVisitor fieldVisitor = new ClassFieldVisitor(Opcodes.ASM5, decVisitor, clazz);
		// DECORATE field visitor with method visitor
		ClassVisitor methodVisitor = new ClassMethodVisitor(Opcodes.ASM5, fieldVisitor, clazz);
		// TODO: add more DECORATORS here in later milestones to accomplish
		// specific tasks
		// Tell the Reader to use our (heavily decorated) ClassVisitor to
		// visit the class
		reader.accept(methodVisitor, ClassReader.EXPAND_FRAMES);

		return clazz;
	}
}