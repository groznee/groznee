import java.util.*;
import java.util.stream.*;
import java.util.concurrent.*;

/* a simple demo implementing the MergeSort alghorithm using Fork Join framework.
It is written for demonstration purposes only, because the Fork Join overheads make  
it no faster (in this particular case) than the sorted() method of a stream #
| https://github.com/groznee/ | */

class ForkJoinSort extends RecursiveTask<List<Integer>> {
	
	private List<Integer> nums = null;
	private int size;
	
	// the constructor takes the list and remembers the size of the list
	ForkJoinSort (List<Integer> nums) {
		this.nums = nums;
		this.size = nums.size();
	};
		
		
	// the main computing method of the RecursiveTask (and RecursiveAction)
	
	protected List<Integer> compute() {
			
		if ( size < 49 ) {
			
		// if the list is smaller than a treshold (49 in this case), sort it directly
		
			return nums.stream().sorted().collect(Collectors.toList());
				
		} else {
			
		// if  not we subdivide the list, a fork it for asynchronous execution		
		
			ForkJoinSort task1 = new ForkJoinSort(nums.subList(0,size/2));
			ForkJoinSort task2 = new ForkJoinSort(nums.subList(size/2,size));
			task1.fork();
			return merge(task2.compute(),task1.join());
			
		}

	}
	
	
	//helper method to merge two sorted lists, utilizing queues
	
	List<Integer> merge (List<Integer> numsTask1, List<Integer> numsTask2) {
		
		Queue<Integer> lowerNums = new LinkedList<>(numsTask1);
		Queue<Integer> higherNums = new LinkedList<>(numsTask2);
		
		List<Integer> subNums = new ArrayList<>();
		int subNumsSize = lowerNums.size()+higherNums.size();
		
		int min = 0;
		
			for ( int i = 0; i < subNumsSize; i++) {
				
				
				// this code compares the numbers in both queues, and ads the lower one to the list
				// if one of the queues is empty, it just adds the other queue to the list and breaks

					if (lowerNums.isEmpty()) {
						subNums.addAll(higherNums);
						break;
					}
					
					if (higherNums.isEmpty()) {
						subNums.addAll(lowerNums);
						break;
					}
					
					min = (lowerNums.peek()<higherNums.peek())? lowerNums.poll() : higherNums.poll();
					subNums.add(min);

			}

		return subNums;
	}
	 
		

}

public class java_se8_fork_join_demo {
	
	public static void main(String[] args) {
			
			List<Integer> numsToSort = new Random().ints(0,100).limit(490).boxed().collect(Collectors.toList());
					
			System.out.println("\nUnsorted list: ");
			
			System.out.println(numsToSort);
			
						
			System.out.println("\nSorted list: ");
			
			ForkJoinSort task = new ForkJoinSort( numsToSort );
			ForkJoinPool Pool = ForkJoinPool.commonPool();
			System.out.println( Pool.invoke(task) );
		

			
	}
}
