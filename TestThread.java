package multiplethread;
  
import java.util.LinkedList;
  
public class ThreadPool {
  
    // 线程池大小
    int threadPoolSize;
  
    // 任务容器
    LinkedList<Runnable> tasks = new LinkedList<Runnable>();
  
    // 试图消费任务的线程
  
    public ThreadPool() {
        threadPoolSize = 10;
  
        // 启动10个任务消费者线程
        synchronized (tasks) {
            for (int i = 0; i < threadPoolSize; i++) {
                new TaskConsumeThread("任务消费者线程 " + i).start();
            }//先启动10个线程,启动后没执行任务.
        }
    }
  
    public void add(Runnable r) {
        synchronized (tasks) {//因为要用notify所以一定要写synchronized一个对象,
            tasks.add(r);
            // 唤醒等待的任务消费者线程
            tasks.notifyAll();
            //这里面一共有3个东西,一个是Threadpool,一个是task,一个是Threadpool里面
            //的容器tasks,threadpool对象运行add就把一个task放入
            //容器tasks里面,然后tasks唤醒线程,唤起占用tasks的线程.
            
            
        }
    }
  
    class TaskConsumeThread extends Thread {//这个是内部类
        public TaskConsumeThread(String name) {
            super(name);//构造方法里面空,
        }
  
        Runnable task;
  
        public void run() {
            System.out.println("启动： " + this.getName());
            while (true) {
                synchronized (tasks) {
                	//队列空,就表示没任务,所以所有占有tasks的线程等待.19行,已经设置好所有的线程
                	//都共享tasks了.所以所有的线程都等待.
                	
                	//如果队列不为空,那么就tasks里面不停的抽出任务,最后一行task.run表示
                	//放到当前类这个线程TaskConsumeThread中运行.
                    while (tasks.isEmpty()) {
                        try {
                            tasks.wait();//利用这个地方来让start暂停的.所以一开始队伍中没有东西就会wait
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    task = tasks.removeLast();
                    // 允许添加任务的线程可以继续添加任务,取出最后一个加入的开始运行他.
                    //所以tasks里面不停的进来一个出去一个,始终在0,1个之间跳跃.
                    tasks.notifyAll();
  
                }
                System.out.println(this.getName() + " 获取到任务，并执行");
                task.run();//运行这个最后加入的task,在当前thread中运行.
            }
        }
    }
  
}
