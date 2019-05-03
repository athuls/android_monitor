import pandas as pd
import numpy as np
import statsmodels.formula.api as sm
import matplotlib.pyplot as plt
import sys

data = pd.read_csv(sys.argv[1])
model = sm.ols(formula='V2 ~ V1', data=data)
fitted = model.fit()
print fitted.summary()
print fitted.pvalues
plt.plot(data["V1"], fitted.fittedvalues)
plt.show()
influence = fitted.get_influence()
(c,p) = influence.cooks_distance
plt.stem(np.arange(len(c)), c, markerfmt=",")
plt.show()
