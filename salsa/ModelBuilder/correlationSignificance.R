r.cint <- function(r,n,level=.95) {
 z <- 0.5*log((1+r)/(1-r))
 zse <- 1/sqrt(n-3)
 zmin <- z - zse * qnorm((1-level)/2,lower.tail=FALSE)
 zmax <- z + zse * qnorm((1-level)/2,lower.tail=FALSE)
 return(c((exp(2*zmin)-1)/(exp(2*zmin)+1),(exp(2*zmax)-1)/(exp(2*zmax)+1)))
}
