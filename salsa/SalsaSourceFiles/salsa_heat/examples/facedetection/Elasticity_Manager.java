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

import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

public class Elasticity_Manager extends UniversalActor  implements ActorService {
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
		Elasticity_Manager instance = (Elasticity_Manager)new Elasticity_Manager(uan, ual,null).construct();
		{
			Object[] _arguments = { args };
			instance.send( new Message(instance, instance, "act", _arguments, null, null) );
		}
	}

	public static ActorReference getReferenceByName(UAN uan)	{ return new Elasticity_Manager(false, uan); }
	public static ActorReference getReferenceByName(String uan)	{ return Elasticity_Manager.getReferenceByName(new UAN(uan)); }
	public static ActorReference getReferenceByLocation(UAL ual)	{ return new Elasticity_Manager(false, ual); }

	public static ActorReference getReferenceByLocation(String ual)	{ return Elasticity_Manager.getReferenceByLocation(new UAL(ual)); }
	public Elasticity_Manager(boolean o, UAN __uan)	{ super(false,__uan); }
	public Elasticity_Manager(boolean o, UAL __ual)	{ super(false,__ual); }

	public Elasticity_Manager(UAN __uan,UniversalActor.State sourceActor)	{ this(__uan, null,null); }
	public Elasticity_Manager(UAL __ual,UniversalActor.State sourceActor)	{ this(null, __ual,null); }
	public Elasticity_Manager(UniversalActor.State sourceActor)		{ this(null, null,null);  }
	public Elasticity_Manager()		{  }
	public Elasticity_Manager(UAN __uan, UAL __ual,Object sourceActor) {
		if (__ual != null && !__ual.getLocation().equals(ServiceFactory.getTheater().getLocation())) {
			createRemotely(__uan, __ual, "examples.facedetection.Elasticity_Manager");
		} else {
			State state = new State(__uan, __ual);
			state.updateSelf(this);
			ServiceFactory.getNaming().setEntry(state.getUAN(), state.getUAL(), state);
			if (getUAN() != null) ServiceFactory.getNaming().update(state.getUAN(), state.getUAL());
		}
	}

	public UniversalActor construct (Reporter_Actor rep_actor, boolean is_reporting, String name_server) {
		Object[] __arguments = { rep_actor, new Boolean(is_reporting), name_server };
		this.send( new Message(this, this, "construct", __arguments, null, null) );
		return this;
	}

	public UniversalActor construct() {
		Object[] __arguments = { };
		this.send( new Message(this, this, "construct", __arguments, null, null) );
		return this;
	}

	public class State extends UniversalActor .State implements salsa.resources.ActorServiceState {
		public Elasticity_Manager self;
		public void updateSelf(ActorReference actorReference) {
			((Elasticity_Manager)actorReference).setUAL(getUAL());
			((Elasticity_Manager)actorReference).setUAN(getUAN());
			self = new Elasticity_Manager(false,getUAL());
			self.setUAN(getUAN());
			self.setUAL(getUAL());
			self.muteGC();
		}

		public State() {
			this(null, null);
		}

		public State(UAN __uan, UAL __ual) {
			super(__uan, __ual);
			addClassName( "examples.facedetection.Elasticity_Manager$State" );
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

		long serialVersionUID = 4L;
		int DEFAULT_MANAGIN_INTERVAL = 1000;
		int DEFAULT_PROFILING_LENGTH = 50;
		Hashtable uan_ualList;
		Hashtable uan_ual_execTimes;
		Hashtable class_uans;
		Hashtable uan_info;
		Hashtable class_bestExecTime;
		Hashtable class_bestExecLocation;
		Reporter_Actor reporter_actor;
		boolean isReporting;
		Elasticity_Manager_Timer timer;
		public void construct(Reporter_Actor rep_actor, boolean is_reporting, String name_server){
			uan_ualList = new Hashtable();
			uan_ual_execTimes = new Hashtable();
			class_uans = new Hashtable();
			uan_info = new Hashtable();
			class_bestExecTime = new Hashtable();
			class_bestExecLocation = new Hashtable();
			reporter_actor = (Reporter_Actor)rep_actor;
			isReporting = is_reporting;
			timer = ((Elasticity_Manager_Timer)new Elasticity_Manager_Timer(new UAN(name_server+"elasticityManagerTimer"),this).construct(this.getUAN().toString()));
		}
		public void profile_action(String uan, String ual, String worker_class, String action, Long time) {
			String location = extract_pure_location(ual);
			String pure_worker_class = extract_pure_class(worker_class);
			if (class_uans.containsKey(pure_worker_class)) {{
				ArrayList tempList = (ArrayList)class_uans.get(pure_worker_class);
				if (!tempList.contains(uan)) {{
					tempList.add(uan);
				}
}			}
}			else {{
				ArrayList tempList = new ArrayList();
				tempList.add(uan);
				class_uans.put(pure_worker_class, tempList);
			}
}			if (uan_ualList.containsKey(uan)) {{
				ArrayList tempList = (ArrayList)uan_ualList.get(uan);
				if (tempList.contains(location)) {{
					tempList.remove(location);
				}
}				tempList.add(location);
			}
}			else {{
				ArrayList tempList = new ArrayList();
				tempList.add(location);
				uan_ualList.put(uan, tempList);
			}
}			String key = uan+"-"+location;
			Integer[] values;
			if (uan_ual_execTimes.containsKey(key)) {{
				values = (Integer[])uan_ual_execTimes.get(key);
			}
}			else {{
				values = new Integer[3];
				for (int i = 0; i<3; i++)values[i] = new Integer(0);
			}
}			values[0] += new Integer(time.intValue());
			values[1] += 1;
			values[2] = new Integer(values[0]/values[1]);
			uan_ual_execTimes.put(key, values);
			int curr_avg_exec_time = values[2];
			uan_info.put(uan, pure_worker_class);
			String k_class = pure_worker_class;
			if (class_bestExecTime.containsKey(k_class)) {{
				Integer best_avg_exec_time = (Integer)class_bestExecTime.get(k_class);
				if (curr_avg_exec_time<best_avg_exec_time) {{
					class_bestExecTime.put(k_class, curr_avg_exec_time);
					class_bestExecLocation.put(k_class, location);
				}
}			}
}			else {{
				class_bestExecTime.put(k_class, curr_avg_exec_time);
				class_bestExecLocation.put(k_class, location);
			}
}		}
		public boolean manage() {
			String temp = "";
			Enumeration e = uan_ualList.keys();
			while (e.hasMoreElements()) {
				String actor_uan = (String)e.nextElement();
				ArrayList tempList = (ArrayList)uan_ualList.get(actor_uan);
				String actor_location = (String)tempList.get(tempList.size()-1);
				String key = actor_uan+"-"+actor_location;
				Integer[] values = (Integer[])uan_ual_execTimes.get(key);
				int curr_execTime = values[2];
				String actor_class = (String)uan_info.get(actor_uan);
				int bestExecTime_class = (Integer)class_bestExecTime.get(actor_class);
				String bestExecLocation_class = (String)class_bestExecLocation.get(actor_class);
				if ((!actor_location.equalsIgnoreCase(bestExecLocation_class))&&(curr_execTime>5*bestExecTime_class)) {{
					Face_Detector target_actor = (Face_Detector)Face_Detector.getReferenceByName(actor_uan);
					if (!bestExecLocation_class.equalsIgnoreCase(extract_pure_location(target_actor.getUAL().toString()))) {{
						{
							// target_actor<-migrate(bestExecLocation_class)
							{
								Object _arguments[] = { bestExecLocation_class };
								Message message = new Message( self, target_actor, "migrate", _arguments, null, null );
								Object[] _propertyInfo = {  };
								message.setProperty( "priority", _propertyInfo );
								__messages.add( message );
							}
						}
						{
							// report("        !!!!!!!!!!!!!!!!!!!!!! Actors Managed ACTOR NEEDS TO MIGRATE "+actor_uan+" to "+bestExecLocation_class+"!!!!!!!!!!!!!!!!")
							{
								Object _arguments[] = { "        !!!!!!!!!!!!!!!!!!!!!! Actors Managed ACTOR NEEDS TO MIGRATE "+actor_uan+" to "+bestExecLocation_class+"!!!!!!!!!!!!!!!!" };
								Message message = new Message( self, self, "report", _arguments, null, null );
								__messages.add( message );
							}
						}
					}
}				}
}			}
			return true;
		}
		public boolean start_manager() {
			{
				// timer<-start_triggering(DEFAULT_MANAGIN_INTERVAL)
				{
					Object _arguments[] = { DEFAULT_MANAGIN_INTERVAL };
					Message message = new Message( self, timer, "start_triggering", _arguments, null, null );
					__messages.add( message );
				}
			}
			{
				// standardOutput<-println("Elasticity_Manager: RUNNING")
				{
					Object _arguments[] = { "Elasticity_Manager: RUNNING" };
					Message message = new Message( self, standardOutput, "println", _arguments, null, null );
					__messages.add( message );
				}
			}
			{
				// report("Elasticity_Manager is RUNNING: <"+this.getUAN().toString()+"> at "+this.getUAL().toString())
				{
					Object _arguments[] = { "Elasticity_Manager is RUNNING: <"+this.getUAN().toString()+"> at "+this.getUAL().toString() };
					Message message = new Message( self, self, "report", _arguments, null, null );
					__messages.add( message );
				}
			}
			return true;
		}
		public void stop_manager() {
			{
				// timer<-stop_triggering()
				{
					Object _arguments[] = {  };
					Message message = new Message( self, timer, "stop_triggering", _arguments, null, null );
					__messages.add( message );
				}
			}
		}
		public void report(String msg) {
			if (isReporting) {			{
				// reporter_actor<-report(msg)
				{
					Object _arguments[] = { msg };
					Message message = new Message( self, reporter_actor, "report", _arguments, null, null );
					__messages.add( message );
				}
			}
}		}
		private String extract_pure_location(String ual) {
			String result = "";
			String s = ual.trim();
			int i = s.indexOf('/');
			if (i<0) {			return new String("ERROR in extracting Location");
}			i = s.indexOf('/', i+1);
			if (i<0) {			return new String("ERROR in extracting Location");
}			i = s.indexOf('/', i+1);
			if (i<0) {			return new String("ERROR in extracting Location");
}			return s.substring(0, i+1);
		}
		private String extract_pure_class(String c) {
			String result = "";
			String s = c.trim();
			int begining = s.indexOf("class");
			if (begining<0) {			return new String("ERROR in extracting Location");
}			s = s.substring(begining+6);
			int last_dot = s.lastIndexOf('.');
			if (last_dot<0) {			return s;
}			else {			return s.substring(last_dot+1);
}		}
	}
}