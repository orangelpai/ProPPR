package edu.cmu.ml.praprolog.prove;

import edu.cmu.ml.praprolog.util.Dictionary;
import edu.cmu.ml.praprolog.util.tuprolog.TuprologAdapter;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Var;

public class TuprologLogicProgramState extends LogicProgramState {
	protected Struct goals, queryGoals;
	protected ProPPRLogicProgramState restartState;
	
	public TuprologLogicProgramState(Goal... goals) {
		this.queryGoals = TuprologAdapter.goalArrayToTerm(goals);
		this.goals = TuprologAdapter.goalArrayToTerm(goals);
		this.restartState = new ProPPRLogicProgramState(goals);
	}
//	public TuprologLogicProgramState(Struct qg, Struct g, Struct og) {
//		this.queryGoals = qg;
//		this.goals = g;
//		this.originalGoals = og;
//	}
//	public static TuprologLogicProgramState fromStartgoals(Struct startgoals) {
//		TuprologLogicProgramState t = new TuprologLogicProgramState();
//		t.queryGoals = startgoals;
//		t.goals = startgoals;
//		t.originalGoals = (Struct) TuprologAdapter.copy(startgoals);
//		return t;
//	}
	public static TuprologLogicProgramState fromState(Term stateterm, ProPPRLogicProgramState restartState) {
		if (! (stateterm instanceof Struct)) throw new IllegalArgumentException("State term not a Struct: "+stateterm);
		Struct state = (Struct) stateterm;
		TuprologLogicProgramState t = new TuprologLogicProgramState();
		t.queryGoals = (Struct) state.getArg(0).getTerm();
		t.goals = (Struct) state.getArg(1).getTerm();
		t.restartState = restartState;
		return t;
	}

	@Override
	public boolean isSolution() {
		return goals.listSize() == 0;
	}

	@Override
	public ProPPRLogicProgramState restart() {
		return new ProPPRLogicProgramState(this.restartState.originalQueryGoals);
	}

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return null;
	}

	public Term getQueryGoals() {
		return this.queryGoals;
	}
	public Term getGoals() {
		return this.goals;
	}
//	public Term getOriginalGoals() {
//		return this.originalGoals;
//	}
	@Override
	public Goal getGroundGoal() {
		if (queryGoals.listSize() != 1) throw new IllegalStateException("1 ground goal expected; found "+queryGoals.listSize());
		return TuprologAdapter.termToGoal(queryGoals.listHead());
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("lpState: ");
		sb.append(this.queryGoals.toString());//,sb," ");
		sb.append(" ... ");
		sb.append(this.goals.toString());//,sb," ");
		return sb.toString();
	}
	
	protected LogicProgramState proppr;
	@Override
	public LogicProgramState asProPPR() {
		if (this.proppr == null) {
			this.proppr = TuprologAdapter.tuprologToProppr(this);
		}
		return this.proppr;
	}
	public Term asTerm() {
		return new Struct("state",queryGoals, goals, TuprologAdapter.goalArrayToTerm(this.restartState.originalQueryGoals));
	}
	@Override
	public String getHeadFunctor() {
		if (this.goals.listSize()==0) return null;
		return ((Struct) this.goals.listHead()).getName();
	}
	@Override
	public Argument getHeadArg1() {
		if (this.goals.listSize()==0) return null;
		return TuprologAdapter.termToArg(((Struct) this.goals.listHead()).getArg(0));
	}
	@Override
	public boolean isHeadEdge() {
		if (this.goals.listSize()==0) return false;
		Struct head = (Struct) this.goals.listHead();
		return head.getArity() == 2
				&& head.getArg(0).getTerm() instanceof Struct
				&& head.getArg(1).getTerm() instanceof Var;
	}
}
