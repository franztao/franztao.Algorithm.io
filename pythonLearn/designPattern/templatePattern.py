'''
Created on Nov 29, 2017

@author: Taoheng
'''
from abc import abstractmethod,ABCMeta

class Game(object):
    __metaclass__ = ABCMeta
      
    @abstractmethod
    def initializ(self):
        pass
    def startPlay(self):
        pass
    def endPlay(self):
        pass
    
    def play(self):
        self.initializ()
        self.startPlay()
        self.endPlay()
      
    
class Cricket(Game):
    def initialize(self):
        print('Cricket Game Initialized! Start playing.')
    def startPlay(self):
        print('Cricket Game Started. Enjoy the game!')
    def endPlay(self):
        print('Cricket Game Finished!')
        
    
    

class Football(Game):
    def initializ(self):
        print('Football  Game Initialized! Start playing.')
    def startPlay(self):
        print('Football  Game Started. Enjoy the game!')
    def endPlay(self):
        print('Football  Game Finished!')



def TemplatePatternDemo():
    game=Cricket();
    game.play()
    
    game=Football();
    game.play()
    
TemplatePatternDemo()
