Minimize
  0 [r,_t2@can] + 0 [r,_t4@r1] + 0 [r,_t1@r3] + 0 [r,_t3@can]
   + 0 [a,_TimingDependency_[id=8]] + 0 [i,_t2@can,_t3@can]
   + 0 [a,_TimingDependency_[id=9]] + 0 [i,_t3@can,_t2@can] + 0 [b,_t2@can]
   + 0 [b,_t3@can]
Subject To
Bounds
 [r,_t2@can] <= 1000
 [r,_t4@r1] <= 1000
 [r,_t1@r3] <= 1000
 [r,_t3@can] <= 1000
 [a,_TimingDependency_[id=8]] free
 [i,_t2@can,_t3@can] <= 1000
 [a,_TimingDependency_[id=9]] free
 [i,_t3@can,_t2@can] <= 1000
 [b,_t2@can] <= 1000
 [b,_t3@can] <= 1000
Binaries
 [a,_TimingDependency_[id=8]] [a,_TimingDependency_[id=9]]
Generals
 [i,_t2@can,_t3@can] [i,_t3@can,_t2@can]
End
