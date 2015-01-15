package edu.iastate.symex.php.nodes;

import java.util.ArrayList;

import org.eclipse.php.internal.core.ast.nodes.Statement;
import org.eclipse.php.internal.core.ast.nodes.SwitchCase;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;
import edu.iastate.symex.datamodel.nodes.DataNodeFactory;
import edu.iastate.symex.datamodel.nodes.LiteralNode;

/**
 * 
 * @author HUNG
 *
 */
public class SwitchCaseNode extends StatementNode {
	
	private ExpressionNode value;
	private LiteralNode condtionString;	
	private ArrayList<StatementNode> statements;
	
	private boolean isDefault;
	private boolean hasBreakStatement;
	
	/*
	Represents a case statement. A case statement is part of switch statement 

	e.g. 
	 case expr:
	   statement1;
	   break;,
	 
	 default:
	   statement2;
	*/
	public SwitchCaseNode(SwitchCase switchCase) {
		super(switchCase);
		
		ArrayList<StatementNode> statementNodes = new ArrayList<StatementNode>();
		boolean hasBreakStatement = false;
		for (Statement statement : switchCase.actions()) {
			StatementNode statementNode = StatementNode.createInstance(statement);
			statementNodes.add(statementNode);
			if (statement.getType() == Statement.RETURN_STATEMENT || statement.getType() == Statement.BREAK_STATEMENT) {
				hasBreakStatement = true;
				break;
			}
		}
		
		this.value = (switchCase.isDefault() ? null : ExpressionNode.createInstance(switchCase.getValue()));
		this.condtionString = (switchCase.isDefault() ? null : DataNodeFactory.createLiteralNode(this.value));
		this.statements = statementNodes;
		this.isDefault = switchCase.isDefault();
		this.hasBreakStatement = hasBreakStatement;
	}
	
	public ExpressionNode getValue() {
		return value;
	}
	
	public LiteralNode getConditionString() {
		return condtionString;
	}
	
	public boolean isDefault() {
		return isDefault;
	}
	
	public boolean hasBreakStatement() {
		return hasBreakStatement;
	}
	
	/**
	 * Adds statement nodes of a switchCaseNode.
	 * Used by SwitchStatementNode only.
	 * @see edu.iastate.symex.php.nodes.SwitchStatementNode.SwitchStatementNode(SwitchStatement)
	 */
	public void addStatementNodes(SwitchCaseNode switchCaseNode) {
		statements.addAll(switchCaseNode.statements);
	}
	
	/**
	 * Removes BreakStatementNode from a SwitchCaseNode.
	 * Used by SwitchStatementNode only.
	 * @see edu.iastate.symex.php.nodes.SwitchStatementNode.SwitchStatementNode(SwitchStatement)
	 */
	public void removeBreakStatementNode() {
		for (int i = 0; i < statements.size(); i++) {
			if (statements.get(i) instanceof BreakStatementNode) {
				while (i < statements.size())
					statements.remove(i);
			}
		}
	}
	
	@Override
	public DataNode execute_(Env env) {
		return BlockNode.executeStatements(statements, env);
	}
	
}
