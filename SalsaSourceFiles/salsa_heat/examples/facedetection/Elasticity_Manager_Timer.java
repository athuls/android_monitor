package examples.facedetection;

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


public class Elasticity_Manager_Timer extends UniversalActor  implements ActorService {
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
		Elasticity_Manager_Timer instance = (Elasticity_Manager_Timer)new Elasticity_Manager_Timer(uan, ual,null).construct();
		{
			Object[] _arguments = { args };
			instance.send( new Message(instance, instance, "act", _arguments, null, null) );
		}
	}

	public static ActorReference getReferenceByName(UAN uan)	{ return new Elasticity_Manager_Timer(false, uan); }
	public static ActorReference getReferenceByName(String uan)	{ return Elasticity_Manager_Timer.getReferenceByName(new UAN(uan)); }
	public static ActorReference getReferenceByLocation(UAL ual)	{ return new Elasticity_Manager_Timer(false, ual); }

	public static ActorReference getReferenceByLocation(String ual)	{ return Elasticity_Manager_Timer.getReferenceByLocation(new UAL(ual)); }
	public Elasticity_Manager_Timer(boolean o, UAN __uan)	{ super(false,__uan); }
	public Elasticity_Manager_Timer(boolean o, UAL __ual)	{ super(false,__ual); }

	public Elasticity_Manager_Timer(UAN __uan,UniversalActor.State sourceActor)	{ this(__uan, null,null); }
	public Elasticity_Manager_Timer(UAL __ual,UniversalActor.State sourceActor)	{ this(null, __ual,null); }
	public Elasticity_Manager_Timer(UniversalActor.State sourceActor)		{ this(null, null,null);  }
	public Elasticity_Manager_Timer()		{  }
	public Elasticity_Manager_Timer(UAN __uan, UAL __ual,Object sourceActor) {
		if (__ual != null && !__ual.getLocation().equals(ServiceFactory.getTheater().getLocation())) {
			createRemotely(__uan, __ual, "examples.facedetection.Elasticity_Manager_Timer");
		} else {
			State state = new State(__uan, __ual);
			state.updateSelf(this);
			ServiceFactory.getNaming().setEntry(state.getUAN(), state.getUAL(), state);
			if (getUAN() != null) ServiceFactory.getNaming().update(state.getUAN(), state.getUAL());
		}
	}

	public UniversalActor construct (String elast_man_UAN) {
		Object[] __arguments = { elast_man_UAN };
		this.send( new Message(this, this, "construct", __arguments, null, null) );
		return this;
	}

	public UniversalActor construct() {
		Object[] __arguments = { };
		this.send( new Message(this, this, "construct", __arguments, null, null) );
		return this;
	}

	public class State extends UniversalActor .State implements salsa.resources.ActorServiceState {
		public Elasticity_Manager_Timer self;
		public void updateSelf(ActorReference actorReference) {
			((Elasticity_Manager_Timer)actorReference).setUAL(getUAL());
			((Elasticity_Manager_Timer)actorReference).setUAN(getUAN());
			self = new Elasticity_Manager_Timer(false,getUAL());
			self.setUAN(getUAN());
			self.setUAL(getUAL());
			self.muteGC();
		}

		public State() {
			this(null, null);
		}

		public State(UAN __uan, UAL __ual) {
			super(__uan, __ual);
			addClassName( "examples.facedetection.Elasticity_Manager_Timer$State" );
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

		long serialVersionUID = 5L;
		Elasticity_Manager elasticity_manager;
		boolean timer_ended;
		public void construct(String elast_man_UAN){
			elasticity_manager = (Elasticity_Manager)Elasticity_Manager.getReferenceByName(elast_man_UAN);
			timer_ended = false;
		}
		public void start_triggering(int interval) {
			if (timer_ended) {{
				return;
			}
}			Token loopToken = new Token("loopToken");
			{
				// token loopToken = elasticity_manager<-manage()
				{
					Object _arguments[] = {  };
					Message message = new Message( self, elasticity_manager, "manage", _arguments, null, loopToken );
					__messages.add( message );
				}
			}
			{
				// ((Elasticity_Manager_Timer)self)<-start_triggering(interval)
				{
					Object _arguments[] = { interval };
					Message message = new Message( self, ((Elasticity_Manager_Timer)self), "start_triggering", _arguments, null, null );
					Object[] _propertyInfo = { new Integer(interval), loopToken };
					message.setProperty( "delayWaitfor", _propertyInfo );
					__messages.add( message );
				}
			}
		}
		public void stop_triggering() {
			timer_ended = true;
		}
	}
}