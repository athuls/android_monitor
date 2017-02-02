package wwc.messaging;

// Import declarations generated by the SALSA compiler, do not modify.
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Vector;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;

import salsa.language.Actor;
import salsa.language.ActorReference;
import salsa.language.Message;
import salsa.language.RunTime;
import salsa.language.ServiceFactory;
import salsa.language.Token;
import salsa.language.exceptions.*;
import salsa.language.exceptions.CurrentContinuationException;

import salsa.language.UniversalActor;

import salsa.naming.UAN;
import salsa.naming.UAL;
import salsa.naming.MalformedUALException;
import salsa.naming.MalformedUANException;

// End SALSA compiler generated import delcarations.

import salsa.language.Placeholder;
import salsa.language.RunTime;
import salsa.language.ServiceFactory;
import salsa.naming.NamingService;
import java.lang.reflect.Constructor;
import salsa.resources.SystemService;
import gc.*;
public class WWCSystem extends UniversalActor implements SystemService {
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
		WWCSystem instance = (WWCSystem)new WWCSystem(uan, ual).construct();
		{
			Object[] _arguments = { args };
			instance.send( new Message(instance, instance, "act", _arguments, null, null) );
		}
	}

	public static ActorReference getReferenceByName(UAN uan)	{ return new WWCSystem(false, uan); }
	public static ActorReference getReferenceByName(String uan)	{ return WWCSystem.getReferenceByName(new UAN(uan)); }
	public static ActorReference getReferenceByLocation(UAL ual)	{ return new WWCSystem(false, ual); }

	public static ActorReference getReferenceByLocation(String ual)	{ return WWCSystem.getReferenceByLocation(new UAL(ual)); }
	public WWCSystem(boolean o, UAN __uan)	{ super(__uan); }
	public WWCSystem(boolean o, UAL __ual)	{ super(__ual); }

	public WWCSystem(UAN __uan)	{ this(__uan, null); }
	public WWCSystem(UAL __ual)	{ this(null, __ual); }
	public WWCSystem()		{ this(null, null);  }
	public WWCSystem(UAN __uan, UAL __ual) {
		if (__ual != null && !__ual.getLocation().equals(ServiceFactory.getTheater().getLocation())) {
			createRemotely(__uan, __ual, "wwc.messaging.WWCSystem",null);
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

	public class State extends UniversalActor.State implements SystemService.State{
		public WWCSystem self;
		public void updateSelf(ActorReference actorReference) {
			self = (WWCSystem)actorReference;
			self.setUAN(getUAN());
			self.setUAL(getUAL());
                        actorReference.setUAL(getUAL());
                        actorReference.setUAN(getUAN());
		}

		public State() {
			this(null, null);
		}

		public State(UAN __uan, UAL __ual) {
			super(__uan, __ual);
			addClassName( "wwc.messaging.WWCSystem$State" );
			addMethodsForClasses();
		}

		public void process(Message message) {
			Method[] matches = getMatches(message.getMethodName());
			Object returnValue = null;
			Exception exception = null;

			if (matches != null) {
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

		NamingService namingService = ServiceFactory.getNaming();
		public void construct(){
		}
		public void messageTargetNotFound(Message message) {
			if (message.getTarget() instanceof ActorReference) {
				ActorReference target = message.getTarget();
				UAN uan = target.getUAN();
				if (uan!=null) {
					UAL ual = target.getUAL();
					namingService.refreshReference(target);
					UAL newUAL = target.getUAL();
					if (newUAL!=null) {
						target.send(message);
						return;
					}
				}
			}
			System.err.println("Message sending error:");
			System.err.println("\tMessage: "+message.toString());
			System.err.println("\tException: Could not find the target actor.");
			System.err.println("\t\tCould not find the UAL at the nameserver.");
		}

                public void messageTargetNotFound(SystemMessage message) {
                                WeakReference target = message.getTarget();
                                UAN uan = target.getUAN();
                                if (uan!=null) {
                                        UAL ual = target.getUAL();
                                        namingService.refreshReference(target);
                                        UAL newUAL = target.getUAL();
                                        if (newUAL!=null) {
                                                target.send(message);
                                                return;
                                        }
                                }

                        System.err.println("System Message sending error:");
                        System.err.println("\tMessage: "+message.toString());
                        System.err.println("\tException: Could not find the target actor.");
                        System.err.println("\t\tCould not find the UAL at the nameserver.");
                }


                public void createActor(UAN uan, UAL ual, String className, Object sourceRef) {
                  Constructor actorConstructor = null;
                  try {
                        Class[] parTypes = { Class.forName("salsa.naming.UAN"), Class.forName("salsa.naming.UAL"), Class.forName("java.lang.Object") };
                        actorConstructor = Class.forName(className).getConstructor(parTypes);
                        Object[] args = { uan, ual, sourceRef };
                        actorConstructor.newInstance(args);
                        //update security info
                        if (uan!=null) {
                          ServiceFactory.getTheater().registerSecurityEntry(uan.toString());
                        }
                  }
                  catch (Exception e) {
                        System.err.println("Remote Actor Creation Exception: ");
                        System.err.println("\tError: "+e);
                        e.printStackTrace();
                  }
                }

                public void createActor(UAN uan, UAL ual, String className) {
                  Constructor actorConstructor = null;
                  try {
                        Class[] parTypes = { Class.forName("salsa.naming.UAN"), Class.forName("salsa.naming.UAL"), Class.forName("java.lang.Object") };
                        actorConstructor = Class.forName(className).getConstructor(parTypes);
                        Object[] args = { uan, ual, null};
                        actorConstructor.newInstance(args);
                        //update security info
                        if (uan!=null) {
                          ServiceFactory.getTheater().registerSecurityEntry(uan.toString());
                        }
                  }
                  catch (Exception e) {
                        System.err.println("Remote Actor Creation Exception: ");
                        System.err.println("\tError: "+e);
                        e.printStackTrace();
                  }
                }


                /*public void addGCAgent(Actor actor) {
                  //Actor previous = namingService.remove(actor.getUAN(), actor.getUAL());
                  //if (previous instanceof Placeholder) {
                  //  ((Placeholder)previous).sendAllMessages();
                  //}

                  namingService.setEntry(actor.getUAN(), actor.getUAL(), actor);
                  namingService.update(actor.getUAN(), actor.getUAL());
                  actor.start();
                  RunTime.receivedUniversalActor();
                }*/

		public void removePlaceholder(UAN uan, UAL ual) {
			Actor removed = namingService.remove(uan, ual);
			if (removed instanceof Placeholder) {
				((Placeholder)removed).sendAllMessages();
			}
                        RunTime.deletedUniversalActor();
		}

		public void addActor(byte[] actorBytes) {
                  Actor actor;
                  RunTime.createdUniversalActor();
                  try {
                    ByteArrayInputStream bis = new ByteArrayInputStream(actorBytes);
                    ObjectInputStream inStream;

                    inStream = new ObjectInputStream(bis);
                    actor= ((Actor) inStream.readObject());
                    inStream.close();
                    bis.close();
                  }
                    catch (Exception e) {
                      RunTime.deletedUniversalActor();
                      System.err.println("WWCSystem Class, addActor() method:Error on deserializing an actor:"+e);
                      return;
                    }

                    Actor previous = namingService.remove(actor.getUAN(), actor.getUAL());
                    if (previous instanceof Placeholder) {
                      ((Placeholder)previous).sendAllMessages();
                    }
                    if (actor.getUAL()==null) {
                      actor.setUAL(namingService.generateUAL());
                    }

                    namingService.setEntry(actor.getUAN(), actor.getUAL(), actor);
                    namingService.update(actor.getUAN(), actor.getUAL());

                    //update security info
                    ServiceFactory.getTheater().registerSecurityEntry(actor.getUAN().toString());
                    actor.start();
                  }
	}
}