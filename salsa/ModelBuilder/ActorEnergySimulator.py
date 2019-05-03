import math
import random
import sys
import numpy as np
from abc import ABCMeta, abstractmethod
import ConfigParser


class Actor:
	__metaclass__ = ABCMeta
	def __init__(self, **kwargs):
		self.all_args = ['util', 'freql', 'freqh', 'cpu_on', 'rdata', 'rchannel',
						'wifil', 'wifih', 'brightness']
		self.energy_params = {key: 0 for key in self.all_args}
		self.args = kwargs
		self.get_parameters()

	def energy_estimate(self):
		cpu = ((self.energy_params['freqh']*4.34 + 
				self.energy_params['freql']*3.42)*self.energy_params['util'] + 
				self.energy_params['cpu_on']*121.46)

		brightness = 2.40*self.energy_params['brightness']
		rchannel = self.energy_params['rchannel']
		beta_wifih = (710 + (48 - 0.768) * rchannel)*rchannel*self.energy_params['rdata']
		wifi = self.energy_params['wifil']*20 + beta_wifih*self.energy_params['wifih']
		energy = cpu + brightness + wifi
		return energy

	@abstractmethod
	def get_parameters(self, **kwargs):
		raise NotImplementedError("You should implement this!")

class CPUActor(Actor):
	
	def get_parameters(self):
		self.energy_params['freql'] = int(self.args['freql'])
		self.energy_params['freqh'] = int(self.args['freqh'])
		dist_args = [int(x) for x in self.args['dist_args'].split(',')]
		self.energy_params['util'] = int(getattr(np.random, self.args['dist'])(*dist_args))
		self.energy_params['cpu_on'] = 1

class LCDActor(Actor):
	

	def get_parameters(self):
		self.energy_params['cpu_on'] = 1
		brightness_args = [int(x) for x in self.args['brightness_args'].split(',')]
		self.energy_params['brightness'] = int(getattr(np.random, self.args['brightness'])(*brightness_args))
		

class WIFIActor(Actor):

	def get_parameters(self):
		self.energy_params['cpu_on'] = 1
		self.energy_params['wifil'] = int(self.args['wifil'])
		self.energy_params['wifih'] = int(self.args['wifih'])


def get_actors(config_dict):
	actors = []
	for actor in config_dict:
		actorclass = getattr(sys.modules[__name__], config_dict[actor]['actor_class'])
		config_dict[actor]['name'] = actor
		actors.append(actorclass(**config_dict[actor]))
	return actors


def parse_config(config_file):
	config = ConfigParser.ConfigParser()
	config.read(config_file)
	config_parser_dict = {s:dict(config.items(s)) for s in config.sections()}
	return config_parser_dict

def simple_random_scheduling(actors, num_cores, num_steps):
	for i in range(int(num_steps)):
		num_actors_to_schedule = np.random.randint(num_cores)
		actors_scheduled = np.random.choice(range(len(actors)), num_cores)
		energy = 0
		actor_summary = ""
		for j in actors_scheduled:
			actors[j].get_parameters()
			energy+= actors[j].energy_estimate()
			actor_summary+=actors[j].args['name']+","
		actor_summary+=str(energy)
		print actor_summary


def simple_saturated_scheduling(actors, num_cores, num_steps):
	for i in range(int(num_steps)):
		num_actors_to_schedule = num_cores
		actors_scheduled = np.random.choice(range(len(actors)), num_cores)
		energy = 0
		actor_summary = ""
		for j in actors_scheduled:
			actors[j].get_parameters()
			energy+= actors[j].energy_estimate()
			actor_summary+=actors[j].args['name']+","
		actor_summary+=str(energy)
		print actor_summary


if __name__ == "__main__":
	name, config_file, num_steps = sys.argv
	config_dict = parse_config(config_file)
	actors = get_actors(config_dict)
	num_cores = 2
	simple_random_scheduling(actors, num_cores, num_steps)
	simple_saturated_scheduling(actors, num_cores, num_steps)
		

