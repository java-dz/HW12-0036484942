package hr.fer.zemris.java.custom.scripting.demo;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.nodes.EchoNode;
import hr.fer.zemris.java.custom.scripting.nodes.ForLoopNode;
import hr.fer.zemris.java.custom.scripting.nodes.INodeVisitor;
import hr.fer.zemris.java.custom.scripting.nodes.TextNode;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParserException;

/**
 * Accepts a file name as a single argument from command line and opens that
 * file, reads its contents and parses it with a {@linkplain SmartScriptParser}
 * into a tree and reproduces its (approximate) original form onto standard
 * output.
 *
 * @author Mario Bobic
 */
public class TreeWriter {

	/**
	 * Program entry point.
	 * 
	 * @param args argument from the command line
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Expected one argument - path to file.");
			return;
		}
		
		String text = getFileText(args[0]);
		SmartScriptParser parser;
		try {
			parser = new SmartScriptParser(text);
		} catch (SmartScriptParserException e) {
			System.err.println("Unable to parse document: " + e.getMessage());
			return;
		}
		
		WriterVisitor visitor = new WriterVisitor();
		parser.getDocumentNode().accept(visitor);
	}

	/**
	 * Returns a String containing text from file with the specified
	 * <tt>path</tt>, or terminates the program if an exception occurs.
	 * 
	 * @param path path of file whose text is to be returned
	 * @return text of the file with the specified path
	 */
	private static String getFileText(String path) {
		try {
			return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
		} catch (Exception e) {
			System.err.println("Exception while reading " + path + ": " + e.getMessage());
			System.exit(-1);
			return null;
		}
	}
	
	/**
	 * A simple writer visitor that recreates nodes and the content they contain
	 * from its children and prints it onto the standard output.
	 *
	 * @author Mario Bobic
	 */
	private static class WriterVisitor implements INodeVisitor {

		@Override
		public void visitTextNode(TextNode node) {
			System.out.println(node);
		}

		@Override
		public void visitForLoopNode(ForLoopNode node) {
			System.out.println(node);
		}

		@Override
		public void visitEchoNode(EchoNode node) {
			System.out.println(node);
		}

		@Override
		public void visitDocumentNode(DocumentNode node) {
			System.out.println(node);
		}
		
	}
	
}
