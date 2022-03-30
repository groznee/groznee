import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import java.util.Map;
import java.util.concurrent.*;
import java.lang.Math;

public class java_se8_streams_demo {

    public static void main(String[] args) {
		
		
		// simple demos using functional interfaces, lambda expressions, streams and concurrency
		// covered in the 1Z0-809 Oracle Java SE 8 exam 
	    	// | https://github.com/groznee/ |
		
		List<Integer> nums = IntStream.range(1, 50).boxed().collect(Collectors.toList());

		System.out.print("The original list: ");
		Consumer<Integer> printIt = x -> System.out.print(x + ", ");
		nums.stream().forEach(printIt);

		System.out.print("\nThe squared list: ");
		UnaryOperator<Integer> doubleIt= x -> x * x;
		nums.stream().map(doubleIt).forEach(printIt);

		System.out.print("\nThe sum of the list: ");
		BinaryOperator<Integer> sumIt = (x,y) -> x + y;
		System.out.print(nums.stream().reduce(0,sumIt));

		System.out.print("\nThe product of the list: ");
		BinaryOperator<Long> productIt = (x,y) -> x * y;
		System.out.print(nums.stream().map(Integer::longValue).reduce(1L, productIt));

		System.out.print("\nMultiples of 3 or 5: ");
		Predicate<Integer> multiplesIt = x -> ( x % 3 == 0  || x % 5 == 0 );
		nums.stream().filter(multiplesIt).forEach(printIt);

		System.out.print("\nSum of the squares: ");
		System.out.print(nums.stream().map(doubleIt).collect(Collectors.summingInt(Integer::intValue)));

		System.out.print("\nSum of the digits of the product: ");
		Long product = nums.stream().map(Integer::longValue).reduce(1L, productIt);
		System.out.print(Long.toString(product).chars().boxed().collect(Collectors.summingInt(Character::getNumericValue)));
	// System.out.println(Long.toString(product).chars().map(x -> x - 48).sum()); //alternative line (numbers start at 48 in the ASCII table, hence -48)
		
		System.out.print("\nPrimes in the list #1: ");
		
	// The code below is written for exercise purposes, it maps each number
	// to a map entry containing the number and a Boolean denoting whether 
	// the number is a prime or not. The primality is calculated by an 
	// anonymous Callable which is executed in a cached pool and returns 
	// the beforementioned Boolean, it implements the 6k+-1 method
		
		ExecutorService executor1 = Executors.newCachedThreadPool();
		
		nums.stream().map( (x)-> {
			Future<Boolean> Primality = executor1.submit(new Callable<Boolean>(){				
					public Boolean call() throws Exception {
						Boolean isPrime = true;
						if ( x <= 3 ) isPrime = (x > 1);
						else if ( ( x % 2 == 0 ) || ( x % 3 == 0 ) ) isPrime = false;
						int factor = 5;
						while (Math.pow(factor,2) <= x ){ 
							if (x % factor == 0 || x % (factor + 2) == 0) isPrime = false;
							factor += 6;
						}
						return isPrime;
					}}
				);
				Boolean wasPrime = false;
				try { wasPrime = Primality.get(); }
				catch (Exception e) { e.printStackTrace(); };
			return new java.util.AbstractMap.SimpleEntry<Integer,Boolean>(x,wasPrime);})
			.forEach( (x) -> {  if (x.getValue()) System.out.print(x.getKey() + ", "); });
			
		executor1.shutdown();
		
		System.out.print("\nPrimes in the list #2: " );
		
	/* Below is a modification of the above, utilizing a List of Futures
	for a slightly more readable code. It exploits the fact that the 
	original list is sequential, if it wasn't the second stream below 
	would utilize a sequential intStream, as the number and its 
	primality are at the same index of two Lists */
		
		ExecutorService executor2 = Executors.newCachedThreadPool();
		
		List<Future<Boolean>> Primality = nums.stream().map(x-> executor2.submit(new Callable<Boolean>(){				
					public Boolean call() throws Exception {
						Boolean isPrime = true;
						if ( x <= 3 ) isPrime = (x > 1);
						else if ( ( x % 2 == 0 ) || ( x % 3 == 0 ) ) isPrime = false;
						int factor = 5;
						while (Math.pow(factor,2) <= x ){ 
							if (x % factor == 0 || x % (factor + 2) == 0) isPrime = false;
							factor += 6;
						}
						return isPrime;
					}}
		)).collect(Collectors.toList());
		
		nums.stream().filter( (x) -> {
				Boolean temp = false;
				try {  temp = Primality.get(x-1).get(); }
				catch (Exception e) { e.printStackTrace(); };
				return temp;
			})
		    .forEach(printIt);

		executor2.shutdown();
		
    }
}
