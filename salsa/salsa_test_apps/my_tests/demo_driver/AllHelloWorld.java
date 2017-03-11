package demo_driver;

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

import java.util.logging.Logger;
import java.util.logging.Level;

public class AllHelloWorld extends UniversalActor  {
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
		AllHelloWorld instance = (AllHelloWorld)new AllHelloWorld(uan, ual,null).construct();
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

	public static ActorReference getReferenceByName(UAN uan)	{ return new AllHelloWorld(false, uan); }
	public static ActorReference getReferenceByName(String uan)	{ return AllHelloWorld.getReferenceByName(new UAN(uan)); }
	public static ActorReference getReferenceByLocation(UAL ual)	{ return new AllHelloWorld(false, ual); }

	public static ActorReference getReferenceByLocation(String ual)	{ return AllHelloWorld.getReferenceByLocation(new UAL(ual)); }
	public AllHelloWorld(boolean o, UAN __uan)	{ super(false,__uan); }
	public AllHelloWorld(boolean o, UAL __ual)	{ super(false,__ual); }
	public AllHelloWorld(UAN __uan,UniversalActor.State sourceActor)	{ this(__uan, null, sourceActor); }
	public AllHelloWorld(UAL __ual,UniversalActor.State sourceActor)	{ this(null, __ual, sourceActor); }
	public AllHelloWorld(UniversalActor.State sourceActor)		{ this(null, null, sourceActor);  }
	public AllHelloWorld()		{  }
	public AllHelloWorld(UAN __uan, UAL __ual, Object obj) {
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
			    createRemotely(__uan, __ual, "demo_driver.AllHelloWorld", sourceRef);
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

	public UniversalActor construct() {
		Object[] __arguments = { };
		this.send( new Message(this, this, "construct", __arguments, null, null) );
		return this;
	}

	public class State extends UniversalActor .State {
		public AllHelloWorld self;
		public void updateSelf(ActorReference actorReference) {
			((AllHelloWorld)actorReference).setUAL(getUAL());
			((AllHelloWorld)actorReference).setUAN(getUAN());
			self = new AllHelloWorld(false,getUAL());
			self.setUAN(getUAN());
			self.setUAL(getUAL());
			self.activateGC();
		}

		public void preAct(String[] arguments) {
			getActorMemory().getInverseList().removeInverseReference("rmsp://me",1);
			{
				Object[] __args={arguments};
				self.send( new Message(self,self, "act", __args, null,null,false) );
			}
		}

		public State() {
			this(null, null);
		}

		public State(UAN __uan, UAL __ual) {
			super(__uan, __ual);
			addClassName( "demo_driver.AllHelloWorld$State" );
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

		int iter;
		Logger logger = Logger.getLogger("ProgressLog");
		public void hello() {
			{
				// standardOutput<-print("Hello ")
				{
					Object _arguments[] = { "Hello " };
					Message message = new Message( self, standardOutput, "print", _arguments, null, null );
					__messages.add( message );
				}
			}
		}
		public void world() {
			{
				// standardOutput<-println("World!")
				{
					Object _arguments[] = { "World!" };
					Message message = new Message( self, standardOutput, "println", _arguments, null, null );
					__messages.add( message );
				}
			}
		}
		public void dummy(int count, int iterCount, int total) {
			logger.log(Level.INFO, "Iteration: "+count+" out of "+total);
			if (total==(count*(iterCount+1))) {{
				logger.log(Level.INFO, "DONE");
			}
}		}
		public void pump(HelloWorld1 world, Nqueens nqueen, String[] argNqueens, Fibonacci fib, String[] argFibonacci, int count, int iterCount, int total) {
			{
				Token token_2_0 = new Token();
				Token token_2_1 = new Token();
				Token token_2_2 = new Token();
				Token token_2_3 = new Token();
				Token token_2_4 = new Token();
				Token token_2_5 = new Token();
				Token token_2_6 = new Token();
				// join block
				token_2_0.setJoinDirector();
				{
					// world<-helloworld()
					{
						Object _arguments[] = {  };
						Message message = new Message( self, world, "helloworld", _arguments, null, token_2_0 );
						Object[] _propertyInfo = { new Integer(1000) };
						message.setProperty( "delay", _propertyInfo );
						__messages.add( message );
					}
				}
				{
					// nqueen<-act(argNqueens)
					{
						Object _arguments[] = { argNqueens };
						Message message = new Message( self, nqueen, "act", _arguments, null, token_2_0 );
						Object[] _propertyInfo = { new Integer(1150) };
						message.setProperty( "delay", _propertyInfo );
						__messages.add( message );
					}
				}
				addJoinToken(token_2_0);
				// join block
				token_2_1.setJoinDirector();
				{
					// world<-helloworld()
					{
						Object _arguments[] = {  };
						Message message = new Message( self, world, "helloworld", _arguments, token_2_0, token_2_1 );
						Object[] _propertyInfo = { new Integer(1300) };
						message.setProperty( "delay", _propertyInfo );
						__messages.add( message );
					}
				}
				{
					// nqueen<-act(argNqueens)
					{
						Object _arguments[] = { argNqueens };
						Message message = new Message( self, nqueen, "act", _arguments, token_2_0, token_2_1 );
						Object[] _propertyInfo = { new Integer(1450) };
						message.setProperty( "delay", _propertyInfo );
						__messages.add( message );
					}
				}
				{
					// fib<-act(argFibonacci)
					{
						Object _arguments[] = { argFibonacci };
						Message message = new Message( self, fib, "act", _arguments, token_2_0, token_2_1 );
						Object[] _propertyInfo = { new Integer(1600) };
						message.setProperty( "delay", _propertyInfo );
						__messages.add( message );
					}
				}
				addJoinToken(token_2_1);
				// join block
				token_2_2.setJoinDirector();
				{
					// world<-helloworld()
					{
						Object _arguments[] = {  };
						Message message = new Message( self, world, "helloworld", _arguments, token_2_1, token_2_2 );
						Object[] _propertyInfo = { new Integer(1750) };
						message.setProperty( "delay", _propertyInfo );
						__messages.add( message );
					}
				}
				{
					// fib<-act(argFibonacci)
					{
						Object _arguments[] = { argFibonacci };
						Message message = new Message( self, fib, "act", _arguments, token_2_1, token_2_2 );
						Object[] _propertyInfo = { new Integer(1900) };
						message.setProperty( "delay", _propertyInfo );
						__messages.add( message );
					}
				}
				addJoinToken(token_2_2);
				// join block
				token_2_3.setJoinDirector();
				{
					// nqueen<-act(argNqueens)
					{
						Object _arguments[] = { argNqueens };
						Message message = new Message( self, nqueen, "act", _arguments, token_2_2, token_2_3 );
						Object[] _propertyInfo = { new Integer(2050) };
						message.setProperty( "delay", _propertyInfo );
						__messages.add( message );
					}
				}
				{
					// fib<-act(argFibonacci)
					{
						Object _arguments[] = { argFibonacci };
						Message message = new Message( self, fib, "act", _arguments, token_2_2, token_2_3 );
						Object[] _propertyInfo = { new Integer(2200) };
						message.setProperty( "delay", _propertyInfo );
						__messages.add( message );
					}
				}
				addJoinToken(token_2_3);
				// join block
				token_2_4.setJoinDirector();
				{
					// fib<-act(argFibonacci)
					{
						Object _arguments[] = { argFibonacci };
						Message message = new Message( self, fib, "act", _arguments, token_2_3, token_2_4 );
						Object[] _propertyInfo = { new Integer(2350) };
						message.setProperty( "delay", _propertyInfo );
						__messages.add( message );
					}
				}
				addJoinToken(token_2_4);
				// join block
				token_2_5.setJoinDirector();
				{
					// nqueen<-act(argNqueens)
					{
						Object _arguments[] = { argNqueens };
						Message message = new Message( self, nqueen, "act", _arguments, token_2_4, token_2_5 );
						Object[] _propertyInfo = { new Integer(2500) };
						message.setProperty( "delay", _propertyInfo );
						__messages.add( message );
					}
				}
				addJoinToken(token_2_5);
				// join block
				token_2_6.setJoinDirector();
				{
					// world<-helloworld()
					{
						Object _arguments[] = {  };
						Message message = new Message( self, world, "helloworld", _arguments, token_2_5, token_2_6 );
						Object[] _propertyInfo = { new Integer(2650) };
						message.setProperty( "delay", _propertyInfo );
						__messages.add( message );
					}
				}
				addJoinToken(token_2_6);
				// dummy(count, iterCount, total)
				{
					Object _arguments[] = { count, iterCount, total };
					Message message = new Message( self, self, "dummy", _arguments, token_2_6, null );
					__messages.add( message );
				}
			}
		}
		public void act(String arguments[]) {
			iter = Integer.parseInt(arguments[0]);
			int queensInput = Integer.parseInt(arguments[1]);
			int fibInput = Integer.parseInt(arguments[2]);
			HelloWorld1[] worlds = new HelloWorld1[iter];
			Integer queensArg = queensInput;
			Integer fibArg = fibInput;
			String argNqueens[] = new String[]{ queensArg.toString(), queensArg.toString() };
			String argFibonacci[] = new String[]{ fibArg.toString() };
			Nqueens nqueens = ((Nqueens)new Nqueens(this).construct());
			Fibonacci fib = ((Fibonacci)new Fibonacci(this).construct(iter*2));
			for (int i = 0; i<iter; i++){
				worlds[i] = ((HelloWorld1)new HelloWorld1(this).construct());
			}
			int count = 0;
			while (count<iter*3) {
				count++;
				for (int i = 0; i<iter; i++){
					{
						// pump(worlds[i], nqueens, argNqueens, fib, argFibonacci, count, i, iter*3*iter)
						{
							Object _arguments[] = { worlds[i], nqueens, argNqueens, fib, argFibonacci, count, i, iter*3*iter };
							Message message = new Message( self, self, "pump", _arguments, null, null );
							__messages.add( message );
						}
					}
				}
			}
		}
	}
}