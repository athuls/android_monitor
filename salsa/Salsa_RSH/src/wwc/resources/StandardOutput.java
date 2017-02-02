package wwc.resources;

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

import salsa.resources.OutputService;
import java.util.Vector;

import java.io.PrintWriter;
import java.io.File; 
import java.io.FileOutputStream;

public class StandardOutput extends UniversalActor  implements OutputService {
       
	private static void printToFile(String mapstring)
	{ 
		String savestr = "atul_testsalsa.log"; 
		File f = new File(savestr);

		PrintWriter out = null;
		try{
			System.err.println("Atul, trying to write to file "+f.getAbsolutePath());
			if ( f.exists() && !f.isDirectory() ) {
    				out = new PrintWriter(new FileOutputStream(new File(savestr), true));
    				out.append(mapstring);
    				out.close();
			}
			else {
    				out = new PrintWriter(savestr);
    				out.println(mapstring);
    				out.close();
			}
		}
		catch(Exception e)
		{
			System.err.println("Atul, error while writing to file with exception: " + e.toString());
		}
	}
	
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
		StandardOutput instance = (StandardOutput)new StandardOutput(uan, ual,null).construct();
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

	public static ActorReference getReferenceByName(UAN uan)	{ return new StandardOutput(false, uan); }
	public static ActorReference getReferenceByName(String uan)	{ return StandardOutput.getReferenceByName(new UAN(uan)); }
	public static ActorReference getReferenceByLocation(UAL ual)	{ return new StandardOutput(false, ual); }

	public static ActorReference getReferenceByLocation(String ual)	{ return StandardOutput.getReferenceByLocation(new UAL(ual)); }
	public StandardOutput(boolean o, UAN __uan)	{ super(false,__uan); }
	public StandardOutput(boolean o, UAL __ual)	{ super(false,__ual); }
	public StandardOutput(UAN __uan,UniversalActor.State sourceActor)	{ this(__uan, null, sourceActor); }
	public StandardOutput(UAL __ual,UniversalActor.State sourceActor)	{ this(null, __ual, sourceActor); }
	public StandardOutput(UniversalActor.State sourceActor)		{ this(null, null, sourceActor);  }
	public StandardOutput()		{  }
	public StandardOutput(UAN __uan, UAL __ual, Object obj) {
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
			    createRemotely(__uan, __ual, "wwc.resources.StandardOutput", sourceRef);
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

	public UniversalActor construct () {
		Object[] __arguments = {  };
		this.send( new Message(this, this, "construct", __arguments, null, null) );
		return this;
	}

	public class State extends UniversalActor .State implements OutputService.State {
		public StandardOutput self;
		public void updateSelf(ActorReference actorReference) {
			((StandardOutput)actorReference).setUAL(getUAL());
			((StandardOutput)actorReference).setUAN(getUAN());
			self = new StandardOutput(false,getUAL());
			self.setUAN(getUAN());
			self.setUAL(getUAL());
			self.activateGC();
		}

		public State() {
			this(null, null);
		}

		public State(UAN __uan, UAL __ual) {
			super(__uan, __ual);
			addClassName( "wwc.resources.StandardOutput$State" );
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

		boolean batteryStatusUpdateRunning = false;
		boolean energyProfilerRunning = false;
		Vector receivers_battery = new Vector();
		Vector receivers_energy = new Vector();
		public void construct(){
		}
		public void print(Object p) {
			StandardOutput.printToFile(String.valueOf(p));
			System.out.print(p);
		}
		public void print(boolean p) {
			StandardOutput.printToFile(String.valueOf(p));
			System.out.print(p);
		}
		public void print(char p) {
			StandardOutput.printToFile(String.valueOf(p));
			System.out.print(p);
		}
		public void print(byte p) {
			StandardOutput.printToFile(String.valueOf(p));
			System.out.print(p);
		}
		public void print(short p) {
			StandardOutput.printToFile(String.valueOf(p));
			System.out.print(p);
		}
		public void print(int p) {
			StandardOutput.printToFile(String.valueOf(p));
			System.out.print(p);
		}
		public void print(long p) {
			StandardOutput.printToFile(String.valueOf(p));
			System.out.print(p);
		}
		public void print(float p) {
			StandardOutput.printToFile(String.valueOf(p));
			System.out.print(p);
		}
		public void print(double p) {
			StandardOutput.printToFile(String.valueOf(p));
			System.out.print(p);
		}
		public void println(Object p) {
			StandardOutput.printToFile(String.valueOf(p));
			System.out.println(p);
		}
		public void println(boolean p) {
			StandardOutput.printToFile(String.valueOf(p));
			System.out.println(p);
		}
		public void println(char p) {
			StandardOutput.printToFile(String.valueOf(p));
			System.out.println(p);
		}
		public void println(byte p) {
			StandardOutput.printToFile(String.valueOf(p));
			System.out.println(p);
		}
		public void println(short p) {
			StandardOutput.printToFile(String.valueOf(p));
			System.out.println(p);
		}
		public void println(int p) {
			StandardOutput.printToFile(String.valueOf(p));
			System.out.println(p);
		}
		public void println(long p) {
			StandardOutput.printToFile(String.valueOf(p));
			System.out.println(p);
		}
		public void println(float p) {
			StandardOutput.printToFile(String.valueOf(p));
			System.out.println(p);
		}
		public void println(double p) {
			StandardOutput.printToFile(String.valueOf(p));
			System.out.println(p);
		}
		public void println() {
			StandardOutput.printToFile("\n");
			System.out.println();
		}
		public void registerBatteryStatusReceiver(UniversalActor r) {
			if (!batteryStatusUpdateRunning) {{
				batteryStatusUpdateRunning = true;
			}
}			if (!receivers_battery.contains(r)) {receivers_battery.add(r);
}		}
		public void broadcastBatteryStatus(boolean isCharging, boolean usbCharge, boolean acCharge, int batteryLevel, int voltage, int temp) {
			for (int i = 0; i<receivers_battery.size(); i++){
				{
					// ((UniversalActor)receivers_battery.get(i))<-battery_status_update(isCharging, usbCharge, acCharge, batteryLevel, voltage, temp)
					{
						Object _arguments[] = { isCharging, usbCharge, acCharge, batteryLevel, voltage, temp };
						Message message = new Message( self, ((UniversalActor)receivers_battery.get(i)), "battery_status_update", _arguments, null, null );
						Object[] _propertyInfo = {  };
						message.setProperty( "priority", _propertyInfo );
						__messages.add( message );
					}
				}
			}
		}
		public void registerEnergyProfilerReceiver(UniversalActor r) {
			if (!energyProfilerRunning) {{
				energyProfilerRunning = true;
			}
}			if (!receivers_energy.contains(r)) {receivers_energy.add(r);
}		}
		public void broadcastEnergyProfilerUpdate(String reporting_title, String reporting_time_interval, double battery_temp, double battery_voltage, double battery_charge, double battery_perc, double instant_power, double avg_power, double current, double total_energy, String[] apps_name, double[] apps_energy_perc, long[] apps_duration, double[] apps_energy_value, String[] apps_comm_name, double[] apps_comm_energy_perc, long[] apps_comm_duration, double[] apps_comm_energy_value) {
			for (int i = 0; i<receivers_energy.size(); i++){
				{
					// ((UniversalActor)receivers_energy.get(i))<-energy_status_update(reporting_title, reporting_time_interval, battery_temp, battery_voltage, battery_charge, battery_perc, instant_power, avg_power, current, total_energy, apps_name, apps_energy_perc, apps_duration, apps_energy_value, apps_comm_name, apps_comm_energy_perc, apps_comm_duration, apps_comm_energy_value)
					{
						Object _arguments[] = { reporting_title, reporting_time_interval, battery_temp, battery_voltage, battery_charge, battery_perc, instant_power, avg_power, current, total_energy, apps_name, apps_energy_perc, apps_duration, apps_energy_value, apps_comm_name, apps_comm_energy_perc, apps_comm_duration, apps_comm_energy_value };
						Message message = new Message( self, ((UniversalActor)receivers_energy.get(i)), "energy_status_update", _arguments, null, null );
						Object[] _propertyInfo = {  };
						message.setProperty( "priority", _propertyInfo );
						__messages.add( message );
					}
				}
			}
		}
	}
}
