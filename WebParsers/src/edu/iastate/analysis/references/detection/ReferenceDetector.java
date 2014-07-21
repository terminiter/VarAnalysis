package edu.iastate.analysis.references.detection;

import java.io.File;

import org.eclipse.wst.jsdt.core.dom.AST;
import org.eclipse.wst.jsdt.core.dom.ASTNode;
import org.eclipse.wst.jsdt.core.dom.ASTParser;

import edu.iastate.analysis.references.ReferenceManager;
import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.symex.analysis.WebAnalysis;
import edu.iastate.symex.constraints.Constraint;
import edu.iastate.symex.position.PositionRange;


/**
 * 
 * @author HUNG
 *
 */
public class ReferenceDetector {
	
	/**
	 * Finds references in PHP code
	 */
	public static void findReferencesInPhpCode(File file, ReferenceManager referenceManager) {
		PhpVisitor phpVisitor = new PhpVisitor(file, referenceManager);
		WebAnalysis.entityDetectionListener = phpVisitor;
	};
	
	public static void findReferencesInPhpCodeFinished() {
		WebAnalysis.entityDetectionListener = null;
	}
	
	/**
	 * Finds references in SQL code
	 */
	public static void findReferencesInSqlCode(String sqlCode, PositionRange sqlLocation, String scope, ReferenceManager referenceManager) {				
        SqlVisitor visitor = new SqlVisitor(sqlCode, sqlLocation, scope, referenceManager);
        visitor.visit();
	}
	
	/**
	 * Finds references in an HtmlDocument
	 */
	public static void findReferencesInHtmlDocument(HtmlDocument htmlDocument, ReferenceManager referenceManager) {    
        HtmlVisitor visitor = new HtmlVisitor(referenceManager);
       	visitor.visitDocument(htmlDocument);
	}
	
	/**
	 * Finds references in JavaScript code
	 */
	public static void findReferencesInJavascriptCode(String javascriptCode, PositionRange javascriptLocation, Constraint constraint, ReferenceManager referenceManager) {				
		ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setSource(javascriptCode.toCharArray());
        ASTNode rootNode = parser.createAST(null);
        
        JavascriptVisitor visitor = new JavascriptVisitor(javascriptLocation, constraint, referenceManager);
        rootNode.accept(visitor);
	}

}