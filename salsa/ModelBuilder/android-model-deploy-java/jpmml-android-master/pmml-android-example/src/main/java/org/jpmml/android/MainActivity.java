/*
 * Copyright (c) 2016 Villu Ruusmann
 *
 * This file is part of JPMML-Android
 *
 * JPMML-Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JPMML-Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with JPMML-Android.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jpmml.android;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Calendar;
import java.io.IOException;

import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import org.dmg.pmml.FieldName;

import android.app.Activity;
import android.app.Dialog;
import android.os.Looper;
import android.os.Handler;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ScrollView;
import org.dmg.pmml.FieldName;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.ModelField;

public class MainActivity extends Activity {

	private ScrollView scrollView = null;
	private TextView textView = null;
	Evaluator evaluator = null;
	double evaluatedVal = 0;
	static Activity context = null;
	
	ArrayList<Map<FieldName, Object>> trainingInputs = null;
	double[] batteryLengths;
	Handler modelHandler;
	Random generator;
/*
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		Button loadButton = (Button)findViewById(R.id.loadButton);

		View.OnClickListener onClickListener = new View.OnClickListener(){

			@Override
			public void onClick(View view){
				Evaluator evaluator;

				try {
					evaluator = createEvaluator();
				} catch(Exception e){
					throw new RuntimeException(e);
				}

				showEvaluatorDialog(evaluator);
			}
		};
		loadButton.setOnClickListener(onClickListener);
	}
*/

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if (scrollView == null) {
			scrollView = new ScrollView( this );
			textView = new TextView( this );
			scrollView.addView( textView );
			// AndroidProxy.setTextViewContext((Activity) this, textView);
		}

		context = (Activity) this;		

		/*
		try {	
			Evaluator evaluator = createEvaluator();	
			showEvaluatorDialog(evaluator);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}*/
		readTrainingFile();
		Thread myThread = new Thread(periodicRunner);
		myThread.start();
		generator = new Random();
	}

	@Override
	public void onStop(){
		super.onStop();
	}
	
	Runnable periodicRunner = new Runnable() {
		@Override
		public void run() {
			try {
				Looper.prepare();
				modelHandler = new Handler();		
				evaluator = createEvaluator();
				modelHandler.post(modelRunner);
				Looper.loop();	
			} catch (Exception e) {
				// handle
			}
		}
	};

	Runnable modelRunner = new Runnable() {
	      @Override
	      public void run() {
		   //int runCount = 0;
	           //while (runCount < 100) {
			//try {
	                //	Thread.sleep(1000); // Waits for 1 second (1000 milliseconds)
			//	if(runCount == 0) { 
			//		evaluator = createEvaluator();
			//	}

		                evaluatedVal = showEvaluatorDialog(evaluator); // make updateAuto() return a string
			//} catch (InterruptedException in) {
				// handle exception
			//} catch (Exception e) {
			//	throw new RuntimeException(e);
			//}

			//runCount++;
			context.runOnUiThread(new Runnable() {
                		public void run() {
					Date currentTime = Calendar.getInstance().getTime();
                    			textView.append("[" + currentTime.toString() + "] " + evaluatedVal + "\n");
					setContentView(scrollView);
                		}
            		});

			modelHandler.postDelayed(modelRunner, 1000);
			/*
	                textView.post(new Runnable() { 
	                     @Override
	                     public void run() {
	                          textView.append(""+evaluatedVal);
				  setContentView(scrollView);
	                     }
			});*/
	           //}
	      }
	};

	private void readTrainingFile() {
		double[] trainMean = {35.616279069767444,2.616279069767442,2.046511627906977,1.5930232558139534,1.244186046511628,1.1511627906976745,1.3255813953488371,1.558139534883721,2.9186046511627906,11.116279069767442,3.046511627906977,0.13953488372093023,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
		double[] trainScale = {19.733202007647147,1.9057355092636308,1.9223715288073966,1.4736273925357999,1.3887930815818845,1.298634633731239,1.3245609257712598,1.483276040688809,2.047204842817892,6.950688378801699,2.1829517492116004,0.40813801442378184,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0};

		String fileName = "overall_nqueens.txt";
		trainingInputs = readInputArgs(fileName, trainMean, trainScale);
		
		// Extract the labels
		batteryLengths = new double[trainingInputs.size()];
		int obs_count = 0;
		for(Map<FieldName, Object> observation : trainingInputs) {
			FieldName label = FieldName.create("BatteryDropLength");
			batteryLengths[obs_count] = Double.parseDouble(observation.get(label).toString());
			obs_count++;
			observation.remove(label);
		}
	}	

	private double showEvaluatorDialog(Evaluator evaluator){
	
		// Run model on training data continuously
		double rmse = 0;
		int runCount = 0;
		try {
			//while(runCount <= 100) {
				int obs_count = 0;
				double mse = 0;
				for(final Map<FieldName, Object> observation : trainingInputs) {
					/*context.runOnUiThread(new Runnable() {
				        	public void run() {
			               			Date currentTime = Calendar.getInstance().getTime();
                				        textView.append("[" + currentTime.toString() + "] " + "\n");
							for (FieldName f : observation.keySet()) {
								textView.append(f.getValue()+" ");
							}
				
							textView.append("\n");
                        	        	        setContentView(scrollView);
	                        	        }
				        });*/

					Map<FieldName, ?> result = evaluator.evaluate(observation);
					Object value = result.get(FieldName.create("BatteryDropTime"));
					final double actualOutput = Double.parseDouble(value.toString());
					final double expectedOutput = batteryLengths[obs_count];
					/*context.runOnUiThread(new Runnable() {
		                                public void run() {
                		                        Date currentTime = Calendar.getInstance().getTime();
                	                	        textView.append("[" + currentTime.toString() + "] " + "Actual: " + actualOutput + " Expected: " + expectedOutput + "\n");
                                        		setContentView(scrollView);
                        		        }
		                        });*/

					mse += Math.pow(actualOutput - expectedOutput, 2);
					obs_count++;
				}

				mse = mse / (double) obs_count;
				rmse = Math.pow(mse,0.5);
			
				// Add randomness to make sure model is being evaluated
				rmse += (generator.nextDouble() - 0.5); 	
				/* if(rmse > 20) {
					break;
				}*/ 
				
			//	Thread.sleep(1000);
			//	runCount++;
					// sb.append("RMSE is higher than expected: ").append(rmse).append("\n");
					/* TextView textViewUser = new TextView(this);

					textViewUser.setText("RMSE is higher than expected: " + rmse + "\n");
					Dialog dialog = new Dialog(this);
					dialog.setContentView(textViewUser);

					dialog.show();
					*/
			//	textView.append("RMSE is higher than expected: " + rmse + "\n");
			//	setContentView(scrollView);
					//break;
			//}
		} catch (final Exception e) {
			// Log exception
			context.runOnUiThread(new Runnable() {
		        	public void run() {
                			Date currentTime = Calendar.getInstance().getTime();
                	                textView.append("[" + currentTime.toString() + "] " + "Exception has been thrown: " + e.toString() + "\n");
                                        setContentView(scrollView);
                                }
		        });
		}
		
		return rmse;
	}

	private ArrayList<Map<FieldName, Object>> readInputArgs(String fileName, double[] train_mean, double[] train_scale) {	
		
		BufferedReader reader = null;
		ArrayList<Map<FieldName, Object>> argumentsList = new ArrayList<Map<FieldName, Object>>();
		try {
		    reader = new BufferedReader(
		        new InputStreamReader(getAssets().open(fileName)));
	
		    // do reading, usually loop until end of file reading  
		    String mLine;
		    while ((mLine = reader.readLine()) != null) {
		       String[] fieldStrings = mLine.split(",");
		       // Final field is the label
		       double[] fields = new double[fieldStrings.length - 1];
		       for (int i = 0; i < fields.length; i++) {
				fields[i] = Double.parseDouble(fieldStrings[i]);
		       }
	
		       double[] scaledInputArgs = scaleInputArgs(fields, train_mean, train_scale);
		       Map<FieldName, Object> argsMap = new HashMap<>();
		       int arg_count = 1;
		       for(double val: scaledInputArgs) {	
		           argsMap.put(FieldName.create("x"+arg_count), val);
			   arg_count++;
		       }
		       
		       // Add the battery drop length/label for this observation
		       argsMap.put(FieldName.create("BatteryDropLength"), Double.parseDouble(fieldStrings[fieldStrings.length - 1])); 
		       argumentsList.add(argsMap);
		    }
		} catch (IOException e) {
			// log the exception
		} finally {
		    if (reader != null) {
		         try {
		             reader.close();
		         } catch (Exception e) {
		             //log the exception
		         }
		    }
		}
		
		return argumentsList;
	}

	private Evaluator createEvaluator() throws Exception {
		AssetManager assetManager = getAssets();

		try(InputStream is = assetManager.open("new_nqueens_rfr_regression_model.pmml.ser")){
			return EvaluatorUtil.createEvaluator(is);
		}
	}

	private double[] scaleInputArgs(double[] inputargs, double[] train_mean, double[] train_scale) {
		double[] scaledArgs = new double[inputargs.length];
		for(int i = 0; i < scaledArgs.length; i++) {
			scaledArgs[i] = (inputargs[i] - train_mean[i])/train_scale[i];
		}	
		
		return scaledArgs;
	}

	static
	private List<FieldName> getNames(List<? extends ModelField> modelFields){
		List<FieldName> names = new ArrayList<>(modelFields.size());

		for(ModelField modelField : modelFields){
			FieldName name = modelField.getName();

			names.add(name);
		}

		return names;
	}
}
