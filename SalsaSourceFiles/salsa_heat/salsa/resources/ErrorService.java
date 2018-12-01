package salsa.resources;

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
// End SALSA compiler generated import delcarations.


public interface ErrorService extends EnvironmentalService {
	public interface State extends Actor {
		public  void print(Object p);
		public  void print(boolean p);
		public  void print(char p);
		public  void print(byte p);
		public  void print(short p);
		public  void print(int p);
		public  void print(long p);
		public  void print(float p);
		public  void print(double p);
		public  void println(Object p);
		public  void println(boolean p);
		public  void println(char p);
		public  void println(byte p);
		public  void println(short p);
		public  void println(int p);
		public  void println(long p);
		public  void println(float p);
		public  void println(double p);
		public  void println();
	}
}