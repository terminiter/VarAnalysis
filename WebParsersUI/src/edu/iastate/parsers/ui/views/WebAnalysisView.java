package edu.iastate.parsers.ui.views;

import java.io.File;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;

import edu.iastate.analysis.references.detection.FindReferencesInFile;
import edu.iastate.analysis.references.detection.ShowStatisticsOnReferences;
import edu.iastate.analysis.references.ReferenceManager;
import edu.iastate.parsers.conditional.CondList;
import edu.iastate.parsers.html.core.DataModelToHtmlTokens;
import edu.iastate.parsers.html.core.HtmlSaxNodesToHtmlDocument;
import edu.iastate.parsers.html.core.HtmlTokensToSaxNodes;
import edu.iastate.parsers.html.dom.nodes.HtmlDocument;
import edu.iastate.parsers.html.generatedlexer.HtmlToken;
import edu.iastate.parsers.html.run.WriteHtmlDocumentToIfDefs;
import edu.iastate.parsers.html.sax.nodes.HtmlSaxNode;
import edu.iastate.parsers.ui.UIHelper;
import edu.iastate.symex.run.RunSymexForFile;
import edu.iastate.symex.ui.views.GenericTreeViewer;
import edu.iastate.symex.datamodel.DataModel;

/**
 * 
 * @author HUNG
 *
 */
public class WebAnalysisView extends ViewPart {

	/*
	 * WebAnalysisView controls
	 */
	private Label filePathLabel;
	
	private Button runAnalysisButton, runSaxParserButton, runDomParserButton;
	
	private TabFolder tabFolder;
	
	private TreeViewer analysisResultTreeViewer, saxResultTreeViewer, domResultTreeViewer;

	private StyledText statsStyledText;
	
	/**
	 * Main method to test the user interface.
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setSize(1000, 250);
		WebAnalysisView dataModelView = new WebAnalysisView();
		dataModelView.createPartControl(shell);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		analysisResultTreeViewer.getControl().setFocus();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(4, false));
		
		filePathLabel = new Label(parent, SWT.NONE);
		filePathLabel.setText("");
		filePathLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		runAnalysisButton = new Button(parent, SWT.PUSH);
	    runAnalysisButton.setText("Run Analysis");
	    runAnalysisButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
	    
	    runSaxParserButton = new Button(parent, SWT.PUSH);
	    runSaxParserButton.setText("Run SaxParser");
	    runSaxParserButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
	    
	    runDomParserButton = new Button(parent, SWT.PUSH);
	    runDomParserButton.setText("Run DomParser");
	    runDomParserButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
	    
		tabFolder = new TabFolder(parent, SWT.BORDER);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4,	1));

		analysisResultTreeViewer = new AnalysisResultTreeViewer(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		saxResultTreeViewer = new SaxResultTreeViewer(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		domResultTreeViewer = new DomResultTreeViewer(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		
		TabItem tabItem1 = new TabItem(tabFolder, SWT.NONE);
		tabItem1.setText("Analysis Result");
		tabItem1.setControl(analysisResultTreeViewer.getControl());
		
		TabItem tabItem2 = new TabItem(tabFolder, SWT.NONE);
		tabItem2.setText("Sax Result");
		tabItem2.setControl(saxResultTreeViewer.getControl());
		
		TabItem tabItem3 = new TabItem(tabFolder, SWT.NONE);
		tabItem3.setText("Dom Result");
		tabItem3.setControl(domResultTreeViewer.getControl());
		
		statsStyledText = new StyledText(tabFolder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL) ;
		statsStyledText.setText("");
		//domStyledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		TabItem tabItem4 = new TabItem(tabFolder, SWT.NONE);
		tabItem4.setText("Analysis Stats");
		tabItem4.setControl(statsStyledText);
		
	    // Event handling
	    // --------------
	    registerEventHandlers();
	}
	
	/**
	 * Registers event handlers
	 */
	private void registerEventHandlers() {
	    runAnalysisButton.addSelectionListener(new SelectionListener() {
	    	
	    	@Override
	    	public void widgetSelected(SelectionEvent event) {
	    		runAnalysisButtonClicked();
	    	}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				runAnalysisButtonClicked();
			}
    	});
	    
	    runSaxParserButton.addSelectionListener(new SelectionListener() {
	    	
	    	@Override
	    	public void widgetSelected(SelectionEvent event) {
	    		runSaxParserButtonClicked();
	    	}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				runSaxParserButtonClicked();
			}
    	});
	    
	    runDomParserButton.addSelectionListener(new SelectionListener() {
	    	
	    	@Override
	    	public void widgetSelected(SelectionEvent event) {
	    		runDomParserButtonClicked();
	    	}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				runDomParserButtonClicked();
			}
    	});
	}
	
	/**
	 * Invoked when the RunAnalysis button is clicked.
	 */
	private void runAnalysisButtonClicked() {
		UIHelper.saveAllEditors();
		if (UIHelper.getActiveEditor() != null) {
			IFile file = UIHelper.getActiveEditorFile();
			runAnalysisAndShowResults(UIHelper.iFileToFile(file));
		}
	}
	
	/**
	 * Invoked when the RunSaxParser button is clicked.
	 */
	private void runSaxParserButtonClicked() {
		UIHelper.saveAllEditors();
		if (UIHelper.getActiveEditor() != null) {
			IFile file = UIHelper.getActiveEditorFile();
			runSaxParserAndShowResults(UIHelper.iFileToFile(file));
		}
	}
	
	/**
	 * Invoked when the RunDomParser button is clicked.
	 */
	private void runDomParserButtonClicked() {
		UIHelper.saveAllEditors();
		if (UIHelper.getActiveEditor() != null) {
			IFile file = UIHelper.getActiveEditorFile();
			runDomParserAndShowResults(UIHelper.iFileToFile(file));
		}
	}
	
	/**
	 * Run Analysis and show results 
	 */
	private void runAnalysisAndShowResults(File file) {
		ReferenceManager referenceManager = new FindReferencesInFile(file).execute();
		
		filePathLabel.setText(file.getAbsolutePath());
		analysisResultTreeViewer.setInput(referenceManager);
		analysisResultTreeViewer.expandToLevel(2);
		tabFolder.setSelection(0);
		statsStyledText.setText(new ShowStatisticsOnReferences().showStatistics(referenceManager));
	}
	
	/**
	 * Run SaxParser and show results 
	 */
	private void runSaxParserAndShowResults(File file) {
		DataModel dataModel = new RunSymexForFile(file).execute();
		CondList<HtmlToken> lexResult = new DataModelToHtmlTokens().lex(dataModel);
		CondList<HtmlSaxNode> saxResult = new HtmlTokensToSaxNodes().parse(lexResult);
		
		filePathLabel.setText(file.getAbsolutePath());
		saxResultTreeViewer.setInput(new GenericTreeViewer.TreeInput(saxResult));
		saxResultTreeViewer.expandToLevel(2);
		tabFolder.setSelection(1);
	}
	
	/**
	 * Run DomParser and show results 
	 */
	private void runDomParserAndShowResults(File file) {
		DataModel dataModel = new RunSymexForFile(file).execute();
		CondList<HtmlToken> lexResult = new DataModelToHtmlTokens().lex(dataModel);
		CondList<HtmlSaxNode> saxResult = new HtmlTokensToSaxNodes().parse(lexResult);
		HtmlDocument domResult = new HtmlSaxNodesToHtmlDocument().parse(saxResult);
		
		filePathLabel.setText(file.getAbsolutePath());
		domResultTreeViewer.setInput(domResult);
		domResultTreeViewer.expandToLevel(2);
		tabFolder.setSelection(2);
		statsStyledText.setText(WriteHtmlDocumentToIfDefs.convert(domResult));
	}
	
}