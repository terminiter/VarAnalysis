package edu.iastate.symex.php.nodes;

import org.eclipse.php.internal.core.ast.nodes.ReturnStatement;

import edu.iastate.symex.core.Env;
import edu.iastate.symex.datamodel.nodes.DataNode;

/**
 * 
 * @author HUNG
 *
 */
public class ReturnStatementNode extends StatementNode {
	
	private ExpressionNode expression;
	
	/*
	Represent a return statement 

	e.g. return;
	 return $a;
	*/
	public ReturnStatementNode(ReturnStatement returnStatement) {
		super(returnStatement);
		expression = (returnStatement.getExpression() != null ? ExpressionNode.createInstance(returnStatement.getExpression()) : null);
	}
	
	@Override
	public DataNode execute(Env env) {
		env.setHasReturnStatement(true);
		if (expression != null) {
			DataNode returnValue = expression.execute(env);
			env.addReturnValue(returnValue);
		}		
		return null;
	}

}