package edu.iastate.analysis.references.detection;

import java.io.File;

import edu.iastate.parsers.html.core.ParseDataModel;
import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.symex.core.PhpExecuter;
import edu.iastate.symex.datamodel.DataModel;
import edu.iastate.symex.util.Timer;
import edu.iastate.symex.util.logging.MyLevel;
import edu.iastate.symex.util.logging.MyLogger;
import edu.iastate.webslice.core.ShowStatisticsOnReferences;

/**
 * 
 * @author HUNG
 *
 */
public class FindReferencesInFile {
	
	public static String PHP_FILE = //"/Work/Eclipse/workspace/scala/VarAnalysis-Tool/runtime-EclipseApplication/Test Project/index.php";
									//"/Work/To-do/Data/Web Projects/Server Code/SchoolMate-1.5.4/index.php";
									//"/Work/To-do/Data/Web Projects/Server Code/addressbookv6.2.12/index.php";
									//"/Work/To-do/Data/Web Projects/Server Code/TimeClock-1.04/index.php";
									//"/Work/To-do/Data/Web Projects/Server Code/UPB-2.2.7/admin_forums.php";
	
									"/Work/Eclipse/workspace/scala/VarAnalysis-Tool/runtime-EclipseApplication/Test Project/testWebSlice.php";
	
	public static String XML_FILE = "/Users/HUNG/Desktop/Dataflows.xml";
	
	private File phpFile;
	
	/**
	 * The entry point of the program.
	 */
	public static void main(String[] args) {
		new FindReferencesInFile(new File(PHP_FILE)).execute();
	}
	
	/**
	 * Constructor.
	 */
	public FindReferencesInFile(File phpFile) {
		this.phpFile = phpFile;
	}
	
	/**
	 * Executes the file.
	 */
	public ReferenceManager execute() {
		Timer timer = new Timer();
		MyLogger.log(MyLevel.PROGRESS, "[FindReferencesInFile:" + phpFile + "] Started.");
		
		// Step 1: Create the data model & find PHP references
		ReferenceManager referenceManager = new ReferenceManager();
		DataModel dataModel = createDataModelAndFindPhpReferences(referenceManager);
		
		// Step 2: Parse the data model
		HtmlDocument htmlDocument = parseDataModel(dataModel);
		
		// Step 3: Find references in the HTML document
		// Comment out the statement below to stop detecting embedded entities.
		findReferencesInHtmlDocument(htmlDocument, referenceManager);
		
		// Step 4: Resolve data flow among the references
		resolveDataFlow(htmlDocument, referenceManager);
		
		// Step 5: Print results
		printResults(referenceManager);
		
		MyLogger.log(MyLevel.PROGRESS, "[FindReferencesInFile:" + phpFile + "] Done in " + timer.getElapsedSecondsInText() + ".");
		
		return referenceManager;
	}
	
	/**
	 * Creates the data model and find PHP references along the way
	 */
	private DataModel createDataModelAndFindPhpReferences(ReferenceManager referenceManager) {
		MyLogger.log(MyLevel.PROGRESS, "[FindReferencesInFile:" + phpFile + "] Creating data model and finding PHP references...");
		
		ReferenceDetector.findReferencesInPhpCode(phpFile, referenceManager);
		
		DataModel dataModel = new PhpExecuter().execute(phpFile);
		
		ReferenceDetector.findReferencesInPhpCodeFinished();
		
		return dataModel;
	}
	
	/**
	 * Parses the dataModel
	 */
	private HtmlDocument parseDataModel(DataModel dataModel) {
		MyLogger.log(MyLevel.PROGRESS, "[FindReferencesInFile:" + phpFile + "] Parsing data model...");
		
		return new ParseDataModel().parse(dataModel);
	}
	
	/**
	 * Finds references in the HtmlDocument.
	 */
	private void findReferencesInHtmlDocument(HtmlDocument htmlDocument, ReferenceManager referenceManager) {
		MyLogger.log(MyLevel.PROGRESS, "[FindReferencesInFile:" + phpFile + "] Finding references from HtmlDocument...");

		ReferenceDetector.findReferencesInHtmlDocument(htmlDocument, phpFile, referenceManager);
	}
	
	/**
	 * Resolves data flow
	 */
	private void resolveDataFlow(HtmlDocument htmlDocument, ReferenceManager referenceManager) {
		MyLogger.log(MyLevel.PROGRESS, "[FindReferencesInFile:" + phpFile + "] Resolving data flow...");
		
		referenceManager.getDataFlowManager().resolveDataFlowforHtmlDocument(htmlDocument);
		referenceManager.getDataFlowManager().resolveDataFlowforEntirePage();
	}
	
	/**
	 * Prints the results
	 */
	private void printResults(ReferenceManager referenceManager) {
		MyLogger.log(MyLevel.PROGRESS, "[FindReferencesInFile:" + phpFile + "] Printing results...");
		
		System.out.println(new ShowStatisticsOnReferences().showStatistics(referenceManager));
		//new XmlReadWrite().printReferencesToXmlFile(referenceManager.getSortedReferenceList(), new File(XML_FILE));
	}
	
}
