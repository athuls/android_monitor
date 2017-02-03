package demo9;

// Import declarations generated by the SALSA compiler, do not modify.
import java.io.IOException;
import java.util.Vector;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;

import salsa.language.Actor;
import salsa.language.ActorReference;
import salsa.language.Message;
import salsa.language.RunTime;
import salsa.language.ServiceFactory;
import gc.WeakReference;
import salsa.language.Token;
import salsa.language.exceptions.*;
import salsa.language.exceptions.CurrentContinuationException;

import salsa.language.UniversalActor;

import salsa.naming.UAN;
import salsa.naming.UAL;
import salsa.naming.MalformedUALException;
import salsa.naming.MalformedUANException;

import salsa.resources.SystemService;

import salsa.resources.ActorService;

// End SALSA compiler generated import delcarations.

import java.io.*;

public class HeatWorker extends UniversalActor  {
	public static void main(String args[]) {
		UAN uan = null;
		UAL ual = null;
		if (System.getProperty("uan") != null) {
			uan = new UAN( System.getProperty("uan") );
			ServiceFactory.getTheater();
			RunTime.receivedUniversalActor();
		}
		if (System.getProperty("ual") != null) {
			ual = new UAL( System.getProperty("ual") );

			if (uan == null) {
				System.err.println("Actor Creation Error:");
				System.err.println("	uan: " + uan);
				System.err.println("	ual: " + ual);
				System.err.println("	Identifier: " + System.getProperty("identifier"));
				System.err.println("	Cannot specify an actor to have a ual at runtime without a uan.");
				System.err.println("	To give an actor a specific ual at runtime, use the identifier system property.");
				System.exit(0);
			}
			RunTime.receivedUniversalActor();
		}
		if (System.getProperty("identifier") != null) {
			if (ual != null) {
				System.err.println("Actor Creation Error:");
				System.err.println("	uan: " + uan);
				System.err.println("	ual: " + ual);
				System.err.println("	Identifier: " + System.getProperty("identifier"));
				System.err.println("	Cannot specify an identifier and a ual with system properties when creating an actor.");
				System.exit(0);
			}
			ual = new UAL( ServiceFactory.getTheater().getLocation() + System.getProperty("identifier"));
		}
		RunTime.receivedMessage();
		HeatWorker instance = (HeatWorker)new HeatWorker(uan, ual,null).construct();
		gc.WeakReference instanceRef=new gc.WeakReference(uan,ual);
		{
			Object[] _arguments = { args };

			//preAct() for local actor creation
			//act() for remote actor creation
			if (ual != null && !ual.getLocation().equals(ServiceFactory.getTheater().getLocation())) {instance.send( new Message(instanceRef, instanceRef, "act", _arguments, false) );}
			else {instance.send( new Message(instanceRef, instanceRef, "preAct", _arguments, false) );}
		}
		RunTime.finishedProcessingMessage();
	}

	public static ActorReference getReferenceByName(UAN uan)	{ return new HeatWorker(false, uan); }
	public static ActorReference getReferenceByName(String uan)	{ return HeatWorker.getReferenceByName(new UAN(uan)); }
	public static ActorReference getReferenceByLocation(UAL ual)	{ return new HeatWorker(false, ual); }

	public static ActorReference getReferenceByLocation(String ual)	{ return HeatWorker.getReferenceByLocation(new UAL(ual)); }
	public HeatWorker(boolean o, UAN __uan)	{ super(false,__uan); }
	public HeatWorker(boolean o, UAL __ual)	{ super(false,__ual); }
	public HeatWorker(UAN __uan,UniversalActor.State sourceActor)	{ this(__uan, null, sourceActor); }
	public HeatWorker(UAL __ual,UniversalActor.State sourceActor)	{ this(null, __ual, sourceActor); }
	public HeatWorker(UniversalActor.State sourceActor)		{ this(null, null, sourceActor);  }
	public HeatWorker()		{  }
	public HeatWorker(UAN __uan, UAL __ual, Object obj) {
		//decide the type of sourceActor
		//if obj is null, the actor must be the startup actor.
		//if obj is an actorReference, this actor is created by a remote actor

		if (obj instanceof UniversalActor.State || obj==null) {
			  UniversalActor.State sourceActor;
			  if (obj!=null) { sourceActor=(UniversalActor.State) obj;}
			  else {sourceActor=null;}

			  //remote creation message sent to a remote system service.
			  if (__ual != null && !__ual.getLocation().equals(ServiceFactory.getTheater().getLocation())) {
			    WeakReference sourceRef;
			    if (sourceActor!=null && sourceActor.getUAL() != null) {sourceRef = new WeakReference(sourceActor.getUAN(),sourceActor.getUAL());}
			    else {sourceRef = null;}
			    if (sourceActor != null) {
			      if (__uan != null) {sourceActor.getActorMemory().getForwardList().putReference(__uan);}
			      else if (__ual!=null) {sourceActor.getActorMemory().getForwardList().putReference(__ual);}

			      //update the source of this actor reference
			      setSource(sourceActor.getUAN(), sourceActor.getUAL());
			      activateGC();
			    }
			    createRemotely(__uan, __ual, "demo9.HeatWorker", sourceRef);
			  }

			  // local creation
			  else {
			    State state = new State(__uan, __ual);

			    //assume the reference is weak
			    muteGC();

			    //the source actor is  the startup actor
			    if (sourceActor == null) {
			      state.getActorMemory().getInverseList().putInverseReference("rmsp://me");
			    }

			    //the souce actor is a normal actor
			    else if (sourceActor instanceof UniversalActor.State) {

			      // this reference is part of garbage collection
			      activateGC();

			      //update the source of this actor reference
			      setSource(sourceActor.getUAN(), sourceActor.getUAL());

			      /* Garbage collection registration:
			       * register 'this reference' in sourceActor's forward list @
			       * register 'this reference' in the forward acquaintance's inverse list
			       */
			      String inverseRefString=null;
			      if (sourceActor.getUAN()!=null) {inverseRefString=sourceActor.getUAN().toString();}
			      else if (sourceActor.getUAL()!=null) {inverseRefString=sourceActor.getUAL().toString();}
			      if (__uan != null) {sourceActor.getActorMemory().getForwardList().putReference(__uan);}
			      else if (__ual != null) {sourceActor.getActorMemory().getForwardList().putReference(__ual);}
			      else {sourceActor.getActorMemory().getForwardList().putReference(state.getUAL());}

			      //put the inverse reference information in the actormemory
			      if (inverseRefString!=null) state.getActorMemory().getInverseList().putInverseReference(inverseRefString);
			    }
			    state.updateSelf(this);
			    ServiceFactory.getNaming().setEntry(state.getUAN(), state.getUAL(), state);
			    if (getUAN() != null) ServiceFactory.getNaming().update(state.getUAN(), state.getUAL());
			  }
		}

		//creation invoked by a remote message
		else if (obj instanceof ActorReference) {
			  ActorReference sourceRef= (ActorReference) obj;
			  State state = new State(__uan, __ual);
			  muteGC();
			  state.getActorMemory().getInverseList().putInverseReference("rmsp://me");
			  if (sourceRef.getUAN() != null) {state.getActorMemory().getInverseList().putInverseReference(sourceRef.getUAN());}
			  else if (sourceRef.getUAL() != null) {state.getActorMemory().getInverseList().putInverseReference(sourceRef.getUAL());}
			  state.updateSelf(this);
			  ServiceFactory.getNaming().setEntry(state.getUAN(), state.getUAL(),state);
			  if (getUAN() != null) ServiceFactory.getNaming().update(state.getUAN(), state.getUAL());
		}
	}

	public UniversalActor construct (double[][] inData) {
		Object[] __arguments = { inData };
		this.send( new Message(this, this, "construct", __arguments, null, null) );
		return this;
	}

	public UniversalActor construct() {
		Object[] __arguments = { };
		this.send( new Message(this, this, "construct", __arguments, null, null) );
		return this;
	}

	public class State extends UniversalActor .State {
		public HeatWorker self;
		public void updateSelf(ActorReference actorReference) {
			((HeatWorker)actorReference).setUAL(getUAL());
			((HeatWorker)actorReference).setUAN(getUAN());
			self = new HeatWorker(false,getUAL());
			self.setUAN(getUAN());
			self.setUAL(getUAL());
			self.activateGC();
		}

		public State() {
			this(null, null);
		}

		public State(UAN __uan, UAL __ual) {
			super(__uan, __ual);
			addClassName( "demo9.HeatWorker$State" );
			addMethodsForClasses();
		}

		public void construct() {}

		public void process(Message message) {
			Method[] matches = getMatches(message.getMethodName());
			Object returnValue = null;
			Exception exception = null;

			if (matches != null) {
				if (!message.getMethodName().equals("die")) {activateArgsGC(message);}
				for (int i = 0; i < matches.length; i++) {
					try {
						if (matches[i].getParameterTypes().length != message.getArguments().length) continue;
						returnValue = matches[i].invoke(this, message.getArguments());
					} catch (Exception e) {
						if (e.getCause() instanceof CurrentContinuationException) {
							sendGeneratedMessages();
							return;
						} else if (e instanceof InvocationTargetException) {
							sendGeneratedMessages();
							exception = (Exception)e.getCause();
							break;
						} else {
							continue;
						}
					}
					sendGeneratedMessages();
					currentMessage.resolveContinuations(returnValue);
					return;
				}
			}

			System.err.println("Message processing exception:");
			if (message.getSource() != null) {
				System.err.println("\tSent by: " + message.getSource().toString());
			} else System.err.println("\tSent by: unknown");
			System.err.println("\tReceived by actor: " + toString());
			System.err.println("\tMessage: " + message.toString());
			if (exception == null) {
				if (matches == null) {
					System.err.println("\tNo methods with the same name found.");
					return;
				}
				System.err.println("\tDid not match any of the following: ");
				for (int i = 0; i < matches.length; i++) {
					System.err.print("\t\tMethod: " + matches[i].getName() + "( ");
					Class[] parTypes = matches[i].getParameterTypes();
					for (int j = 0; j < parTypes.length; j++) {
						System.err.print(parTypes[j].getName() + " ");
					}
					System.err.println(")");
				}
			} else {
				System.err.println("\tThrew exception: " + exception);
				exception.printStackTrace();
			}
		}

		int x;
		int y;
		double[][] data;
		double[][] next;
		HeatWorker top;
		HeatWorker bottom;
		boolean topOk = false;
		boolean bottomOk = false;
		void construct(double[][] inData){
			x = inData.length+2;
			y = inData[0].length;
			data = new double[x][];
			data[0] = new double[y];
			data[x-1] = new double[y];
			for (int j = 0; j<y; j++){
				data[0][j] = data[x-1][j]=-1;
			}
			for (int i = 0; i<x-2; i++){
				data[i+1] = new double[y];
				System.arraycopy(inData[i], 0, data[i+1], 0, y);
			}
			next = new double[x][];
			for (int i = 0; i<x; i++){
				next[i] = new double[y];
				System.arraycopy(data[i], 0, next[i], 0, y);
			}
		}
		public void connectTop(HeatWorker w) {
			top = w;
			{
				// top<-bottomRow(data[0])
				{
					Object _arguments[] = { data[0] };
					Message message = new Message( self, top, "bottomRow", _arguments, null, null );
					__messages.add( message );
				}
			}
		}
		public void connectBottom(HeatWorker w) {
			bottom = w;
			{
				// bottom<-topRow(data[x-1])
				{
					Object _arguments[] = { data[x-1] };
					Message message = new Message( self, bottom, "topRow", _arguments, null, null );
					__messages.add( message );
				}
			}
		}
		public void topRow(double[] row) {
			System.arraycopy(row, 0, data[0], 0, y);
			topOk = true;
		}
		public void bottomRow(double[] row) {
			System.arraycopy(row, 0, data[x-1], 0, y);
			bottomOk = true;
		}
		public double[][] doWork(int iterations) {
			if (((bottom==null)||(bottomOk))&&((top==null)||(topOk))) {{
				int from = (top==null?2:1);
				int to = (bottom==null?x-2:x-1);
				for (int i = from; i<to; i++)for (int j = 1; j<y-1; j++)next[i][j] = 0.25*(data[i-1][j]+data[i][j-1]+data[i+1][j]+data[i][j+1]);
				double[][] temp = next;
				next = data;
				data = temp;
				if (top!=null) {				{
					// top<-bottomRow(data[0])
					{
						Object _arguments[] = { data[0] };
						Message message = new Message( self, top, "bottomRow", _arguments, null, null );
						__messages.add( message );
					}
				}
}				if (bottom!=null) {				{
					// bottom<-topRow(data[x-1])
					{
						Object _arguments[] = { data[x-1] };
						Message message = new Message( self, bottom, "topRow", _arguments, null, null );
						__messages.add( message );
					}
				}
}				topOk = false;
				bottomOk = false;
				if (iterations==0) {{
					double[][] results = new double[x-2][];
					System.arraycopy(data, 1, results, 0, x-2);
					return results;
				}
}				else {{
					{
						// doWork(new Integer(iterations-1))
						{
							Object _arguments[] = { new Integer(iterations-1) };
							Message message = new Message( self, self, "doWork", _arguments, null, currentMessage.getContinuationToken() );
							__messages.add( message );
						}
						throw new CurrentContinuationException();
					}
				}
}			}
}			else {{
				{
					// doWork(new Integer(iterations))
					{
						Object _arguments[] = { new Integer(iterations) };
						Message message = new Message( self, self, "doWork", _arguments, null, currentMessage.getContinuationToken() );
						__messages.add( message );
					}
					throw new CurrentContinuationException();
				}
			}
}		}
	}
}