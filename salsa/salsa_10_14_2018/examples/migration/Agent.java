package examples.migration;

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

import java.util.Vector;

public class Agent extends UniversalActor  implements ActorService {
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
		Agent instance = (Agent)new Agent(uan, ual,null).construct();
		{
			Object[] _arguments = { args };
			instance.send( new Message(instance, instance, "act", _arguments, null, null) );
		}
	}

	public static ActorReference getReferenceByName(UAN uan)	{ return new Agent(false, uan); }
	public static ActorReference getReferenceByName(String uan)	{ return Agent.getReferenceByName(new UAN(uan)); }
	public static ActorReference getReferenceByLocation(UAL ual)	{ return new Agent(false, ual); }

	public static ActorReference getReferenceByLocation(String ual)	{ return Agent.getReferenceByLocation(new UAL(ual)); }
	public Agent(boolean o, UAN __uan)	{ super(false,__uan); }
	public Agent(boolean o, UAL __ual)	{ super(false,__ual); }

	public Agent(UAN __uan,UniversalActor.State sourceActor)	{ this(__uan, null,null); }
	public Agent(UAL __ual,UniversalActor.State sourceActor)	{ this(null, __ual,null); }
	public Agent(UniversalActor.State sourceActor)		{ this(null, null,null);  }
	public Agent()		{  }
	public Agent(UAN __uan, UAL __ual,Object sourceActor) {
		if (__ual != null && !__ual.getLocation().equals(ServiceFactory.getTheater().getLocation())) {
			createRemotely(__uan, __ual, "examples.migration.Agent");
		} else {
			State state = new State(__uan, __ual);
			state.updateSelf(this);
			ServiceFactory.getNaming().setEntry(state.getUAN(), state.getUAL(), state);
			if (getUAN() != null) ServiceFactory.getNaming().update(state.getUAN(), state.getUAL());
		}
	}

	public UniversalActor construct () {
		Object[] __arguments = {  };
		this.send( new Message(this, this, "construct", __arguments, null, null) );
		return this;
	}

	public class State extends UniversalActor .State implements salsa.resources.ActorServiceState {
		public Agent self;
		public void updateSelf(ActorReference actorReference) {
			((Agent)actorReference).setUAL(getUAL());
			((Agent)actorReference).setUAN(getUAN());
			self = new Agent(false,getUAL());
			self.setUAN(getUAN());
			self.setUAL(getUAL());
			self.muteGC();
		}

		public State() {
			this(null, null);
		}

		public State(UAN __uan, UAL __ual) {
			super(__uan, __ual);
			addClassName( "examples.migration.Agent$State" );
			addMethodsForClasses();
		}

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

		Vector itinerary = new Vector();
		UAL initialLocation;
		long initialTime;
		int hops;
		void construct(){
			initialLocation = this.getUAL();
			initialTime = System.currentTimeMillis();
			hops = 0;
						{
				// standardOutput<-println("Initial Location: "+this.getUAL())
				{
					Object _arguments[] = { "Initial Location: "+this.getUAL() };
					Message message = new Message( self, standardOutput, "println", _arguments, null, null );
					__messages.add( message );
				}
			}
		}
		public void addLocation(String ual) {
			hops++;
			itinerary.add(new UAL(ual));
		}
		public void printItinerary() {
			System.err.println("Current Time: "+System.currentTimeMillis());
			Token x = new Token("x");
			{
				// token x = standardOutput<-println("Itinerary: ")
				{
					Object _arguments[] = { "Itinerary: " };
					Message message = new Message( self, standardOutput, "println", _arguments, null, x );
					__messages.add( message );
				}
			}
			for (int i = 0; i<itinerary.size(); i++){
				{
					Token token_3_0 = new Token();
					// standardOutput<-print("\t"+(UAL)itinerary.get(i))
					{
						Object _arguments[] = { "\t"+(UAL)itinerary.get(i) };
						Message message = new Message( self, standardOutput, "print", _arguments, null, token_3_0 );
						Object[] _propertyInfo = { x };
						message.setProperty( "waitfor", _propertyInfo );
						__messages.add( message );
					}
					// x = standardOutput<-println()
					Token x_next = new Token("<-_next");
					{
						Object _arguments[] = {  };
						Message message = new Message( self, standardOutput, "println", _arguments, token_3_0, x_next );
						__messages.add( message );
					}
					x = x_next;
				}
			}
			{
				// standardOutput<-println()
				{
					Object _arguments[] = {  };
					Message message = new Message( self, standardOutput, "println", _arguments, null, currentMessage.getContinuationToken() );
					Object[] _propertyInfo = { x };
					message.setProperty( "waitfor", _propertyInfo );
					__messages.add( message );
				}
				throw new CurrentContinuationException();
			}
		}
		public void printTime() {
			Token x = new Token("x");
			{
				Token token_2_0 = new Token();
				// standardOutput<-println("Migrated "+hops+" times.")
				{
					Object _arguments[] = { "Migrated "+hops+" times." };
					Message message = new Message( self, standardOutput, "println", _arguments, null, token_2_0 );
					__messages.add( message );
				}
				// token x = standardOutput<-println("\tUAL: "+getUAL())
				{
					Object _arguments[] = { "\tUAL: "+getUAL() };
					Message message = new Message( self, standardOutput, "println", _arguments, token_2_0, x );
					__messages.add( message );
				}
			}
			if (getUAL().equals(initialLocation)) {{
				{
					// x = standardOutput<-println("Time ellapsed: "+new Long(System.currentTimeMillis()-initialTime))
					Token x_next = new Token("<-_next");
					{
						Object _arguments[] = { "Time ellapsed: "+new Long(System.currentTimeMillis()-initialTime) };
						Message message = new Message( self, standardOutput, "println", _arguments, null, x_next );
						Object[] _propertyInfo = { x };
						message.setProperty( "waitfor", _propertyInfo );
						__messages.add( message );
					}
					x = x_next;
					// standardOutput<-println("Migration avg time: "+new Long((System.currentTimeMillis()-initialTime)/hops))
					{
						Object _arguments[] = { "Migration avg time: "+new Long((System.currentTimeMillis()-initialTime)/hops) };
						Message message = new Message( self, standardOutput, "println", _arguments, x_next, null );
						__messages.add( message );
					}
				}
			}
}			{
				// standardOutput<-println()
				{
					Object _arguments[] = {  };
					Message message = new Message( self, standardOutput, "println", _arguments, null, currentMessage.getContinuationToken() );
					Object[] _propertyInfo = { x };
					message.setProperty( "waitfor", _propertyInfo );
					__messages.add( message );
				}
				throw new CurrentContinuationException();
			}
		}
		public void act(String[] args) {
			try {
				Agent agent = ((Agent)new Agent(new UAN(args[0]), null,this).construct());
				Token synchToken = new Token("synchToken");
				{
					// token synchToken = agent<-printItinerary()
					{
						Object _arguments[] = {  };
						Message message = new Message( self, agent, "printItinerary", _arguments, null, synchToken );
						__messages.add( message );
					}
				}
				for (int i = 1; i<args.length; i++){
					{
						Token token_4_0 = new Token();
						Token token_4_1 = new Token();
						Token token_4_2 = new Token();
						// agent<-migrate(args[i])
						{
							Object _arguments[] = { args[i] };
							Message message = new Message( self, agent, "migrate", _arguments, null, token_4_0 );
							Object[] _propertyInfo = { synchToken };
							message.setProperty( "waitfor", _propertyInfo );
							__messages.add( message );
						}
						// agent<-addLocation(args[i])
						{
							Object _arguments[] = { args[i] };
							Message message = new Message( self, agent, "addLocation", _arguments, token_4_0, token_4_1 );
							__messages.add( message );
						}
						// agent<-printItinerary()
						{
							Object _arguments[] = {  };
							Message message = new Message( self, agent, "printItinerary", _arguments, token_4_1, token_4_2 );
							__messages.add( message );
						}
						// synchToken = agent<-printTime()
						Token synchToken_next = new Token("<-_next");
						{
							Object _arguments[] = {  };
							Message message = new Message( self, agent, "printTime", _arguments, token_4_2, synchToken_next );
							__messages.add( message );
						}
						synchToken = synchToken_next;
					}
				}
			}
			catch (Exception e) {
				{
					// standardOutput<-println(e)
					{
						Object _arguments[] = { e };
						Message message = new Message( self, standardOutput, "println", _arguments, null, null );
						__messages.add( message );
					}
				}
				{
					// standardOutput<-println("Usage: java migration.Agent <UAN> (<UAL>)*")
					{
						Object _arguments[] = { "Usage: java migration.Agent <UAN> (<UAL>)*" };
						Message message = new Message( self, standardOutput, "println", _arguments, null, null );
						__messages.add( message );
					}
				}
			}

		}
	}
}