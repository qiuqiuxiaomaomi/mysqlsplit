# mysqlsplit
## mysql 分库分表

![](https://i.imgur.com/amWjT1O.jpg)

<pre>
   基本分库分表:
       1:分库分表
       2:分库表冗余
       3:分区表
   分布式事务
       1:XA分布式事务
       2:TCC分布式事务
       3:消息分布式事务
</pre>

<pre>
   Mycat分片规则
   Mycat读写分离
   Mycat故障切换
   Mycat+Percona+Haproxy+keepalived
   Zookeeper搭建Mycat高可用集群
   Mycat注解技术
   Mycat性能监控
   Mycat架构剖析
     1) Mycat线程架构
     2) Mycat网络IO架构
     3) Mycat内存还礼与缓存架构
     5) Mycat连接池 主从切换
   Mycat核心技术
     1）Mycat路由的实现
     2）Mycat跨库join的实现
     3）Mycat数据汇聚和排序
</pre>

![](https://i.imgur.com/LHh2xss.jpg)

Mysql复制的工作方式

![](https://i.imgur.com/gByifSu.jpg)

Mysql查询执行路径

![](https://i.imgur.com/uWe1SWM.jpg)

<pre>
   什么限制了Mysql的性能
      最常见的两个瓶颈：
      1）CPU
	     当数据存储全部存储在内存中，CPU会成为瓶颈
	  2）IO资源
	     一般发生在工作所需的数据远远超过有效内存容量的时候
	   
      硬件性能的优化
         1）低延时
           更快的CPU
         2）高吞吐
           更多的CPU个数,要取决于Mysql版本，不同版本使用CPU的个数是有极限的
      Mysql复制也能在高速CPU下工作非常快，而多CPU对复制帮助却不大，主库上的并发任务传递到备库以后会被简化为串行任务，也就是是备库的瓶颈通常是IO子系统，而不是CPU

      在硬件层面，一个查询可以再执行或者等待，处于等待状态的原因是在运行队列中等待，等待磁盘，等待网络，等待闩(Latch)或者锁(Lock),
         1）如果等待闩或者锁，通常需要更快的CPU
         2）如果在运行队列中等待，那么更多或者更快的CPU都可能有帮助

   1）Cpu架构 CPU核心
      Mysql的扩展模式是指它可以有效利用CPU的数量，以及在压力不断增长的情况下如何扩展，这同时取决于工作负载和系统架构，CPU架构（RISC,CISC,流水线深度），CPU型号，操作系统都影响Mysql的扩展模式

      现代CPU的复杂之处：
         1）频率调整
	     2）boost技术

   2）平衡内存和硬盘资源
      配置大量内存的最大原因其实不是因为可以在内存中保存大量的数据：
         1）最终目的是避免磁盘I/O，因为磁盘I/O比在内存中访问数据要慢的多
	     2）关键是要平衡内存和磁盘的大小。速度，成本和其他因素。
      
      随机I/O，顺序I/O
	     顺序操作的执行速度比随机I/O的操作快，无论是在内存还是在硬盘
         存储引擎执行顺序读比随机读快

      缓存的写入延迟特性：
         1）多次写入，一次刷新
	     2）I/O合并
      预写日志（WAL）策略：采用在内存中变更页面，而不马上刷新到磁盘的策略，后续时机再刷新到磁盘
         缓存命中率实际上也会决定使用多少CPU，所以评估缓存命中率的最好方法是查看CPU的利用率，若CPU使用了99%的时间工作，1%的时间等待，那么缓存的命中率还是不错的。
      
      1）闪存，
      2）固态硬盘，
      3）PCIE存储设备

   3）RAID性能优化,SAN/NAS
      SAN和NAS是两个外部文件存储设备加载到服务器的方法
      SAN基于块访问，使得所有服务器的硬盘像访问一块硬盘
      NAS基于文件的协议来访问

   6）操作系统 文件系统 磁盘调度策略  
      线程  内存交换区
      操作系统选择：
          Linux发行版，最好的策略是使用专为服务器应用程序设计的发行版，而不是桌面版本。
      文件系统：
      通常建议使用XFS文件系统
      选择磁盘调度策略
      内存交换区
      监控操作系统的工具：
          vmstat iostat	

   7）复制
       1）基于语句的复制
       2）基于行的复制
       基于行的复制和基于语句的复制都是通过在主库上记录二进制日志，在备库中重放日志的方式实现异步的数据复制
       低版本的Mysql不能作为高版本Mysql的备库，而高版本的Mysql可以作为低版本Mysql的备库，Mysql具有向后兼容性，高版本的Mysql可以兼容低版本的Mysql

       复制的三步骤
          1）主库把数据更改记录到二进制文件(Binary log)中
             在每次准备提交事务完成数据更新前，主库将数据更新的事件记录到二进制文件中，Mysql会按照事务提交的顺序而非每条语句执行的顺序记录二进制日志记录完二进
             制日志后，主库会告诉粗怒触引擎可以提交事务了。
          2）备库将主库上的日志复制到自己的中继日志(relay Log)中
             备库会启动一个工作线程，成为I/O线程，I/O线程跟主库建立一个普通的客户端连接，然后在主库上启动一个特殊的二进制转储线程，这个二进制转储线程会
	         读取主库上二进制日志中的时间，它不会对事件进行轮训。如果该线程追上了主库，它将进入休眠状态，备库I/O线程会将接收到的事件记录到中继日志中。
          3）备库读取中继日志中的时间，将其重放到备库数据之上

       5）复制事件
       6) 复制问题
          1）数据损坏与丢失
             1）主库意外关闭
             2）备库意外关闭
             3）主库上的二进制文件意外损坏
             5）备库上的中继日志损坏
             6）二进制日志与InnoDB事务日志不同步

          2）使用非事务性表
          3）不确定语句
          5）主库备库使用不同的存储引擎
          6）不唯一的服务器ID
          7）丢失的零时表
          8）Innodb加锁读引起的锁争议
          9）过大的复制延迟
          10）来自主库过大的包
          11）受限的复制带宽
          12）备库发生数据改变
          13) Mysql复制的高级特性

   8）复制拓补结构
       1）一主多备
       2）主动模式下的主主复制，被动模式下的主主复制
       3）环形复制
       5）其他复制方案

   9）分区表、视图 外键约束 游标 全文索引 查询缓存
       分区表是一个独立的逻辑表，底层由多个物理子表组成，创建表时通过partion by子句定义

       Mysql实现分区表的方式是对底层的疯转，意味着索引也是按照分区的字表定义的，而没有全局索引Mysql在创建表的时候使用PARTION BY子句定义每个分区存放的数据。在执行查询的时候，优化器会根据分区定义过滤那些没有我们需要数据的分区，这样查询就无需扫描所有分区。分区的一个主要目的是将数据按照一个较粗的粒度分在不同的表中，这样做可以将相关的数据存放在一起，另外，如果想一次批量删除整个分区的数据也会变得方便。

       使用场景
		    1）表非常大以至于无法全部放入内存中，或者只有表的最后部分是热点数据，其他均是历史数据
			2）分区表的数据更容易维护。
			3）分区表的数据可以分布在不同的物理设备上，从而高效的利用各种物理设备
			5）可以使用分区表来避免某些特殊的瓶颈，例如InnoDB的单个索引的互斥访问
			6）如果需要，还可以备份和恢复独立的分区，这在数据量非常大的数据集的场景下效果非常好。
			一个表最多只能有1024个分区
			分区表中无法使用外键约束

       分区表的原理：
           Select查询时，分区表先打开并锁住所有的底层表了，优化器先判断是否可以顾虑掉部分分区，然后再调用存储引擎接口访问各个分区的数据
	       Insert操作，分区层先打开并锁住所有的底层表，然后确定那个分区接收这个记录，再将记录写入对应的底层表
	       Delete操作，当删除一条记录时，分区层先打开并锁住所有的底层表，然后确定数据对应的分区，最后对相应的底层表进行删除操作

       	视图：
	       视图本身是一个虚拟表，不存放任何数据。在使用SQL语句访问视图时候，它返回的数据是Mysql从其他表中生成的。不能对视图创建触发器，也不能使用DROP TABLE命令删除视图。

        视图对性能的影响。
           多数人认为视图不能提升性能，实际上，在Mysql中某些情况下视图也可以帮助提升性能。

        外键的约束：
		    InnoDB是目前Mysql中唯一支持外键的内置存储引擎。
			使用外键是有成本的，比如外键通常都要求每次在修改数据时都要在另外一张表中多执行一次查询操作，虽然InnoDB强制外键使用索引，但还是无法消除
			约束检查的开销。如果外键列的选择性很低，则会导致一个非常大且选择性很低的索引。
			在某些场景下，外键会提升一些性能。如果想确保两个相关表始终有一致的数据，那么使用外键比在应用程序中检查一致性的性能高得多。
			外键约束使得查询额外访问一些别的表，这也意味着需要额外的锁。如果向子表中写入一条记录，外键约束会让InnoDB检查对应的父表的记录，也就需要对
			父表对应记录进行加锁操作来确保这条记录不会在这个事务完成之时就被删除了。这会导致额外的锁等待，甚至会导致死锁，由于没有直接访问这些表，
			所以这类锁问题往往很难排查

        在Mysql内部存储代码：
		    Mysql允许通过触发器，存储过程，函数的形式来存储代码
			这四种存储代码都是用特殊的SQL语句扩展，它包含了很多过程处理语法，例如循环和条件分支等。
			在Mysql中使用存储代码的优缺点：
			    优点：
				     1）它在服务器内部执行，离数据最近，另外在服务器上执行还可以节省带宽和网络延迟。
					 2）这是一种代码重用，可以方便地统一业务规则，保证某些行为总是一致，所以也可以为应用提供一定的安全性
					 3）它可以简化代码的维护和版本更新
					 5）可以帮助提升安全，比如提供更细粒度的权限控制
					 6）服务器可以缓存存储过程的执行计划，这对于需要反复调用的过程，会大大降低消耗
					 7）因为是在服务器端部署的，所以备份，维护都可以在服务器端完成。
					 8）可以再应用开发和数据库开发人员之间更好的分工。不过最好是由数据库专家来开发存储过程，因为不是每个应用开发人员
					    都能写出高效的SQL查询
						
				缺点：
		            1）Mysql本身没有提供好用的开发和调试工具，所以编写Mysql的存储代码比其他数据库要更难一些
		            2）较之应用程序的代码，存储代码效率要稍微差些例如，存储代码中可以使用的函数非常有限，所以使用存储代码很难编写
		            复杂的字符串维护功能，也很难实现太复杂的逻辑
		            3）存储代码可能会给应用程序代码的部署带来额外的复杂性。
		            5）因为存储程序部署在服务器内，所以可能偶遇安全隐患
		            6）存储过程会给数据库服务带来额外的压力，而数据库服务器的扩展性相比应用服务器要差很多。
		            7）Mysql并没有什么选项可以控制存储程序的资源消耗，所以在存储过程中的一个小错误可能把服务器拖死
		            8）存储代码在MYSQL中的实现也有很多限制
		            9）存储过程的调式是建很困难的事情。

           字符集和校对规则如果影响查询
                某些字符集和校对规则可能需要更多的CPU操作，可能会消耗更多的内存和存储空间。			
       
   10）高性能索引的策略
       在Mysql中，索引是在存储引擎层而不是在服务层实现的，不同存储引擎的索引的工作方式并不一样，也不是所有的存储引擎都支持所有类型的索引。及时多个存储引擎支持同一种类型的索引，其底层的实现也可能不同。

       索引的类型:
	       1) B-Tree
	          大多数的MYSQL引擎都支持B-Tree索引，Archive引擎是一个例外，Archive不支持任何索引。
	          
	          MyIsam使用前缀压缩技术使得索引更小
	          InnoDB则按照原数据格式进行存储
	          MyIsam索引通过数据的物理位置引用被索引的行
	          Innodb则根据主键引用被索引的行
	
	       2) Hash索引
	          只有memory引擎支持hash索引,memory引擎是支持非唯一hash索引的，这在数据库世界里面是比较与众不同的，如果多个列的hash值相同，索引会以连表的方式存放多个记录
	          指针到同一个hash条目中。
	
	          因为索引自身只需要存储对应的hash值，所以索引的结构非常紧凑，这也让hash索引查找的速度非常快，然而hash索引也有限制
	          1）hash索引数据并不是按照索引值顺序存储的，所以也就无法用于排序
	          2）hash索引页不支持部分索引列匹配查找。
	          3）hash索引只支持比较查询，不支持范围查询
	          5）hash索引存在hash冲突
	          6）hash冲突如果很多的话，维护索引的代价也很高
	
	          InnoDB引擎有一个特殊的功能叫做"自适应hash索引"，当InnoDB注意到某些索引值被使用得非常频繁时，它会在内存中基于B-Tree索引之上再创建一个hash索引，这样就让B-Tree索引也具有hash索引的一些优点，比如快速的hash查找 ，这是完全自动的内部的行为，用户无法感知或者配置，用户也可以关闭该功能。
	
	       3) R-Tree空间数据索引
	          MyIsam表支持空间索引，可以用作地理数据存储，和B-Tree索引不同，这类索引无需前缀索引。空间索引会从所有维度来索引数据。查询时，可以有效使用任意维度来组合查询。
	
	       5) 全文索引
	          全文索引更类似于搜索引擎做的事情，而不是简单的where条件匹配，需要主要停用词，词干，布尔搜索等

       索引的优点
           1）索引大大减少了服务器需要扫描的数据量
           2）索引可以帮助服务器避免排序和零时表
           3）索引可以将随机I/O变为顺序I/O

       高性能索引策略
           1）独立的列
           2）前缀索引，索引选择性
              对于BLOB, TEXT, VARCHAR类型的列，必须使用前缀索引，因为MYSQL不允许索引这些列的全部,需要选择合适的前缀长度

              前缀索引是一种能够是索引更小，更快的有效办法，但是另一方面也有其缺点，MYSQL无法使用前缀索引做order by和group by,也无法使用前缀索引做覆盖扫描
           
           3）多列索引
           5）选择合适的索引列顺序
              在一个多列B-Tree索引中，索引列的顺序意味着索引首先按照最左列进行排序，其次是第二列，等等，所以索引可以按照升序或者降序扫描，以满足精确符合列顺序的ORDER BY, GROUP BY, DISTINCT等子句的查询要求。
              多列索引的列顺序经验法则：
                  当不需要排序和分组时，将选择性最高的列放到索引最前列

           6）聚簇索引
              聚簇索引并不是一种单独的索引类型，而是一种数据的存储方式,在Innodb中的聚簇索引实际上在同一个结构中保存了B-Tree索引和数据行。

              当表中有聚簇索引时，它的数据行实际上存放在索引的叶子页中，因为无法同时把数据行存放在两个不同的地方，所以一个表只能有一个聚簇索引。因为是存储引擎负责实现索引，所以不是所有的存储引擎都支持聚簇索引。InnoDB通过主键聚集数据。
   
              如果没有定义主键，InnoDB会选择一个唯一的非空索引代替，如果没有这样的索引，InnoDB会隐式的定义一个主键待作为聚簇索引。

              聚簇索引可能对性能有帮助，也可能带来严重的性能问题：
                 优点：
                    1）可以把相关数据保存在一起
                    2）数据访问更快 
                 缺点：
                    1）聚簇索引最大限度的提高了I/O密集型应用的性能，但是如果数据全部都放在内存中，则访问的顺序就没那么重要了，聚簇索引也就没有优势了
                    2）插入速度严重依赖插入顺序，按照主键的顺序插入数据是加载数据到InnoDB表中速度最快的方式，但是如果不是按照主键顺序加载数据，那么在加载完成之后最好用optimize table命令重新组织一下表
                    3）更新局促索引列的代价很高，因为会强制InnoDB将每个被更新的行移动到新的位置
                    5）基于聚簇索引的表在插入行，或者主键被更新导致需要移动行的时候，可能面临"页分裂"的问题，页分裂会导致占用更多的磁盘空间
                    6）聚簇索引可能导致全表扫描变慢，尤其是行比较稀疏，或者由于页分裂导致数据存储不连续的时候
                    7）二级索引（非聚簇索引）可能比想象中的要更大，因为二级索引的叶子节点包含了引用行的主键列
                    8）二级索引的访问需要两次索引查找。二级索引叶子节点保存的不是指向行的物理位置的指针而是行的主键值
               MyIsam和InnoDB的数据分布比较  
 
           7）覆盖索引
                  如果一个索引包含所有需要查询的字段的值，我们就称之为覆盖索引 
                  优势：
                     1）索引条目通常小于数据行大小，所以如果只需要读取索引，那MYSQL就会极大地较少数据访问量。覆盖索引对于I/O密集型的应用也有帮助，因为索引比数据小，更容易全部放入内充
	                 2）因为索引是按照列值顺序存储的，所以对于I/O密集型的范围查询会比随机从磁盘读取每一行数据的I/O要少得多。
	                 3）一些存储引擎如MyISAM在内存中只缓存索引，数据则依赖与操作系统来缓存，因此访问数据需要一次系统调用。
	                 5）InnoDB的聚簇索引，覆盖索引对InnoDB特别有用，InnoDB的二级索引在叶子节点中保存了行的主键值，所以如果二级主键能够覆盖查询，则可以避免对主键索引的二次查询。

                   覆盖索引必须要存储索引列的值，而哈希索引，空间索引，全文索引都不能存储索引列的值，所以MySQL只能使用B-TREE索引做覆盖索引。而且不同的索引实现覆盖索引的方式也不同，而且不是所有的引擎都支持覆盖索引。                       

           8）使用索引扫描来排序
              MySql有两种方式可以生成有序的结果：1）通过排序操作  2）或者按索引顺序扫描扫描索引本身是很快的，因为只需要从一条索引记录移动到紧接着的下一条记录，但如果扫描索引不能覆盖查询所需的全部列，那就不得不每扫描一条索引记录就都回表查询一次对应的行，这基本上都是随机I/O,Mysql可以使用同一个索引既满足排序有用于查询。因此如果可能，设计索引时应该尽可能满足这两种任务，这样是最好的。

              只有当索引的列顺序和Order by子句的顺序万一一致，并且所有列的排序方向（顺序或正序）都一样时，MySQL才能够使用索引来对结果做排序。如果查询需要关联多张表，则只有当order by子句引用的字段全部为第一个表时，才能使用索引做排序。

           9）压缩索引
              MyIsam 使用前缀压缩来减少索引的大小，从而让更多的索引可以放入内存中，这在某些情况下能极大的提高性能，默认只压缩字符串，但通过参数设置也可以对整数做压缩。
              MyIsam压缩每个索引块的方法是：
                  先完全保存索引块的第一个值，然后将其他值和第一个值进行比较得到相同前缀的字节数和剩余的不同后缀部分，把这部分存储起来即可。压缩块使用更少的空间，代价是某些操作可能更慢，因为每个值的压缩前缀都依赖前面的值，所以MyIsam查找时无法在索引块使用二分查找而只能从头开始扫描，正序的扫描速度还不错，但是如果是倒序扫描就不是很好了，所以在块中查找某一行的操作平均都需要扫描半个索引块。	 

           10）冗余和重复索引
               Mysql允许在相同列上创建多个索引，无论是有意的还是无意的，Mysql需要单独维护重复的索引，并且优化器在优化查询时也需要逐个地进行考虑，这会影响性能。
               重复索引是在相同列上按照相同的顺序创建相同类型的索引，应该避免这样创建这样重复的索引，发现以后也应该立即删除。 

               大多数情况下都不需要冗余索引，应该尽量扩展已有的索引而不是创建新索引，但也有时候处于性能方面的考虑需要 冗余索引，因为扩展已有的索引会导致其变得太大，从而影响其他使用索引的查询的性能。

           11）索引和锁
               索引可以让查询锁定更少的行，如果你的查询从不访问那些不需要的行，那么就会锁定更少的行。
               从两个方面来看这对性能都有好处。
                  1）首先，虽然InnoDB的行锁效率很高，内存使用也很少，但是锁定行的时候仍然会带来额外开销；
                  2）锁定超过需要的行会增加锁征用并较少并发性

                  InnoDB只有在访问行的时候才会对其加锁，而索引能够减少InnoDB访问的行数，从而较少锁的数量。
                  InnoDB在二级索引上使用共享（读）锁，但访问主键索引需要排他（写）锁。

   11）Schema与数据类型优化
   12）服务器性能剖析
   13）Mysql查询性能优化
       1）慢查询优化
          1）确认应用程序是否在检索大量的超过需要的数据，这通常意味着访问了太多的行，但有时候有可能是由于访问了太多的列。
          2）确认MYSQL服务层是否在分析大量超过需要的数据行 
          
          典型案例及解决方案：
             1）查询不需要的数据，解决方法，使用limit
             2）多表关联是返回全部列，解决方法，只取需要的列
             3）总是取出全部列 slect * ，取出全部列，会让优化器无法完成索引覆盖扫描这类优化，还会为服务器带来额外的I/O，内存，CPU的消耗。         
             5）重复查询相同的数据

          对Mysql来说，最简单的衡量查询开销有三个指标
          1）响应时间
	         1）服务时间: 数据库处理这个查询真正花费的时间
		     2）排队时间：服务因为等待某些资源而没有真正执行查询的时间,可能是等待I/O操作也可能是等待行锁
	      2）扫描的行数
	      3）返回的行数
	         较短的行的访问速度更快，内存中的行也比磁盘上的行的访问速度要快的多。

       2）重构查询
          一个复杂查询还是多个简单查询：
              在传统查询中，总是强调需要数据库层完成尽可能多的工作，这样做的逻辑在于以前总是认为网络工薪，查询解析和优化是一件代价很高的事情，但是这样的想法对于Mysql并不适用，Mysql从设计上让连接和断开连接都很轻量级，在返回一个小的查询结果方面很高效，现代的网络速度比以前快很多。

              不过，在应用设计的时候，如果一个查询能够胜任时还写成多个独立查询是不明智的。

          1）切分查询
             有时候对于一个大查询我们需要"分而治之"，将大查询切分成小查询，每个查询功能完全一样，只完成一小部分，每次只返回一小部分查询结果。

             例如，一次删除一万条数据和分10次删除，每次删除1000条数据。

          2）分解关联查询
             很多高性能的应用都会对关联查询进行分解，简单地，可以对每一个表进行一次单表查询，然后将结果在应用程序中进行关联。
             事实上，使用分解关联查询的方式重构查询有如下优势：
                 1）让缓存的效率更高，如果某个表很少改变，那么基于该表的查询就可以重复利用查询缓存结果了
	             2）将查询分解后，执行单个查询可以减少锁的竞争
	             3）在应用层关联，可以更容易对数据库进行拆分，更容易做到高性能和可扩展
	             5）查询本身效率也可能有所提升，
	             6）可以减少冗余记录的查询，在应用层做关联查询，意味着对于某条记录应用只需要查询一次，而在数据库中做关联查询时，则可能需要重复的访问一部分数据，从这点看，这样的重构还可能较少网络和内存的消耗
	             7）更进一步，这样做相当于在应用中实现了hash关联，而不是使用Mysql的嵌套循环关联，某些场景hash关联的效率要高很多。

        3) Mysql客户端/服务器通信协议
           Mysql客户端和服务器之间的通信协议是半双工的。这种协议让Mysql通信简单快速，但是也从很多地方限制了Mysql，一个明显的限制是，这意味着没法进行流量控制，一旦一端开始发生消息，另一端要接收完整个消息才能响应它，

        5) 查询缓存
           在解析一个查询语句前，如果查询缓存是打开的，那么Mysql会优先检查这个查询是否命中查询缓存中的数据。这个检查是通过一个队大小写敏感的hash查找实现的，查询和缓存中的查询即使只有一个字节不同，那也不会匹配缓存结果，这种情况查询就会进入下一阶段的处理。

           如果当前的查询恰好命中了查询缓存，那么在返回查询结果之前Mysql会检查一次用户权限，这仍然是无需解析查询SQL语句的，以为查询缓存中已经存放了当前查询需要访问
           的表信息，如果权限没有问题，Mysql会跳过所有其他阶段，直接从缓存中拿到结果并返回给客户端，这种情况下，查询不会被解析，不会生成查询计划，不会被执行。

        6) 查询优化处理：
           1）语法解析器和预处理
	          Mysql通过关键字将SQL语句进行解析，并生成一棵对应的"解析树",Mysql解析器将使用MYSQL语法规则验证和解析验证，例如验证是否使用错误的关键字，或者使用关键字的顺序是否正确等，再或者它还会验证引号是否能前后正确匹配，
	   
	          预处理器则根据一些MYSQL规则进一步检查解析书是否合法，例如，这里将检查数据表和数据列是否存在，还会解析名字和别名，看看是否有歧义，

           2) 查询优化器
              Mysql使用基于成本的优化器，它将尝试预测一个查询使用某种执行计划时的成本，并选择其中成本最小的一个。
			  例如：>select * from bonaparte_charge
			       >show status like 'Last_query_cost'
				  
				   每个表或者索引的页面个数，索引的基数，索引和数据行的长度，索引分布情况都会影响性能
				   
				   很多情况导致Mysql执行错误的执行计划
				   1）统计信息不准确，Mysql依赖存储引擎提供的统计信息来评估成本，但是有的存储引擎提供的统计信息不准确
				   2）执行计划中的成本估算不等同于实际执行的成本，所以即使统计信息准确，优化器给出的执行计划也可能不是最优的。
				     有时候某个执行计划虽然需要读取更多的页面，但是它的成本却更小，因为如果这些页面都是书序读取或者这些页面都已经存在于内存中的
					 话，那么它的访问成本将很小，Mysql层面并不知道哪些页面在内存中，哪些页面在磁盘上，所以查询实际执行过程中到底需要多少次IO是无法预知的。
				   3）Mysql的最优可能与你想象的最优不一样
				     Mysql只是基于成本模型选择的执行计划，而有些时候并不是最优的执行计划
				   5）Mysql从不考虑其它并发执行的查询，这可能会影响当前查询的速度
				   6）Mysql也并不是任何时候都是基于成本的优化，有时候也会基于一定的规则
				     例如如果存在全文搜索的MATCH()子句，则在存在全文索引的时候就是用全文索引，即使其他使用where性能要高   
				   7）Mysql不会考虑不受气控制的操作的成本
				     例如执行存储过程或者用户自定义函数的成本

                Mysql的查询优化器是一个非常复杂的部件，它使用了很多优化策略来生成一个最优的执行计划，优化策略可以简单的分为：
				1）静态优化
				   静态优化可以直接对解析书进行分析，并完成优化，静态优化不依赖特别的值，是一种编译时优化
				2）动态优化
				   与查询的上下文有关，也可能和很多其它因素相关，这需要在每次查询的时候都进行评估，可以认为是"运行时优化"
				
				Mysql能够处理的优化类型
				1）重新定义关联表的顺序
				2）将外连接转化为内连接
				3）使用等价变换规则
				5）优化Count(),min(),max()
				6）预估并转化为常数表达式
				7）覆盖索引扫描
				8）子查询优化
				9）提前终止查询，例如使用了limit
				10）等值传播
				11）列表in()的比较
				    Mysql将In()列表中的数据先进行排序，然后通过二分查找的方式来确定列表中的值是否满足条件，这是一个O(logn)的复杂度，而or查询是一个O(n)的复杂度，而查询是一个对于In()表中有大量取值时，Mysql的出来速度更快。
				    Mysql还会做其它大量的优化，核心观点就是"不要自以为比优化器更聪明"

                返回结果给客户端
                    如果查询可以被缓存，那么MYSQL在这个阶段也会将结果存放在查询缓存中。
	                Mysql将结果集返回给客户端是一个增量的，逐步返回的过程，一旦一个执行计划执行开始，开始生成第一条查询记录，Mysql就可以开始想客户端返回结果集了，结果集中的每一行都会以一个满足MYSQL 客户端/服务器通信协议的封包发送，再通过TCP协议进行传输，在TCP传输的过程中，可能对MYSQL的封包进行缓存并批量传输。

                通过在查询中加入提示（hint），就可以控制该查询的执行计划。
                    HIGH_PRIORITY和LOW_PRIORITY
                    DELAYED
                    STRAIGHT_JOIN
                    SQL_SMALL_RESULT和SQL_BIG_RESULT
                    SQL_CACHE和SQL_NOCACHE
                    SQL_CALC_FOUND_ROWS
                    FOR UPDATE和LOCK IN SHARE MODE
                    USE INDEX,IGNORE INDEX,FORCE INDEX
                    optimizer_search_depth
                    optimizer_prune_level
                    optimizer_switch

                优化特定类型的查询

   15) Mysql的存储引擎
       1) Innodb
       2) MyIsam
       3) Archive
       5) memory
       6) NDB集群引擎 
   16) Mysql的锁粒度
   17) Mysql的事务隔离级别
   18) 多版本并发控制MVCC
</pre>

<pre>
InnoDB与MyIsam引擎的区别与应用场景
      1）事务处理
         InnoDB支持事务，MyIsam不支持事务
      2）锁机制不同
         MyIsam是表级锁，InnoDB是行级锁
      3）select, update, insert, delete操作
         MyIsam如果执行大量的select，MyIsam是更好的选择
         InnoDB如果执行大量的Insert或Update，处于性能的考虑，应该使用InnoDB
      5) 查询表的行数不同
         MyIsam:select count(*) from table 只需要简单的独处保存好的行数，
         InnoDB: InnoDB不保存表的具体行数
      6）外键支持
         MyIsam不支持外键
         InnoDB支持外键

     为什么MyIsam会比InnoDB的查询速度快
         InnoDB在做select的时候，要维护的东西比MyIsam引擎多很多；
         1）数据块，InnoDB要缓存，MyIsam只缓存索引块，这中间还有换进换出的减少。
         2）InnoDB寻址要映射到块，再到行，MyIsam记录的直接是文件的OffSet，定位比InnoDB要快。
         3）InnoDB还需要维护MVCC一致，虽然不适用，但它还是需要检查和维护

     应用场景
         MyIsam适合：
           1) 做很多count的计算
           2）插入不频繁，查询非常频繁
           3）没有事务
         InnoDB:
           1) 可靠性要求比较高，或者要求事务
           2）表更新和查询都相当的频繁，并且行锁定的机会比较大的情况。 

     InnoDB适合线上事务性的场景
     MyIsam适合数据仓库的场景
</pre>

![](https://i.imgur.com/THPpCRt.png)

<pre>
    在大规模的Mysql服务器集群中，如果是存在一台主服务，多台从服务器，在繁忙的业务中，意味
    着主服务器每操作一个事件，都要往自己的二进制日志中写数据，同时还要往多台从服务器发一
    次，N台服务器指向一台主服务器，那么需要主服务器发送N次，会启动N个线程，每个线程各自从
    线程里读二进制日志，那么会有大量的IO，本来是为主服务器减轻负担的，那么这样只能造成压
    力越来越大，那这样master主机就会为每台slave主机分配出一个binlog dump进程，这样的话
    会严重影响master的性能

    MySQL在5.x系列提供了Blackhole引擎“黑洞”，任何写入到此引擎的数据均会被丢弃掉，不做实际
    存储。

    解决这种问题可以采用多级复制，主服务器还是保持主位置A，再拿一台服务器作为从服务器B，
    主服务器A只启动一个线程指向从服务器B，那么B服务器再作为其他N台服务器的主，那么B就启动
    了多个线程，怎么给B服务器减轻压力呢？

    在主从之间添加一个分布式master，配置blackhole存储引擎，他起到一个中继的作用，他接收
    数据但丢其他而不是存储，只是会把master的二进制日志供下层的slave来读取。

       第一，让B服务器不再执行查询操作；
       第二，让B服务器不再执行写操作；
       第三，负责多线程为每个从服务器提供数据，那么就不需要在B服务器存储数据了，但是需要
            提供二进制日志和中继日志，但B服务器又不需要数据库

       blackhole引擎它不占硬盘空间，作为一个中转，只负责记日志、传日志。
</pre>

<pre>
CSV 引擎
      CSV存储引擎可以将csv文件作为mysql的表进行处理，存储格式就是普通的csv文件。
</pre>

<pre>
第三方存储引擎
      1）Infobright
         MySQL的列存储引擎，适用于数据分析和数据仓库设计。
             1）查询性能高
                比普通的MyIsam, InnoDB快5-60倍；
             2）存储数据量特别大
             3）高压缩比
                与普通的数据库存放的数据文件相比，压缩比达到55:1
             5）不需要建立索引

         缺点：
             不能高并发，最多10个并发

      2）TokuDB
             支持数据压缩，支持高速写入的引擎，但是不适合update多的场景。
</pre>

<pre>
Oracle与Mysql的差异
      1）并发性
         Mysql以表级锁为主，对资源锁定的粒度很大。虽然InnoDB的引擎可以使用行级锁，但这个行
      级锁的机制依赖与表的索引，如果表没有索引，或者SQL语句没有使用索引，那么任然使用表级锁。
     
         Oracle使用行级锁，对资源锁定的粒度小很多，加锁是在数据库的行上，不依赖于索引。所以
      Oracle对并发性的支持要好很多。

      
      2）一致性
         Oracle支持serializable的隔离级别，可以实现最高级别的读一致性。每个session提交
      后，其他session才能看到提交的更改。oracle通过在undo表空间构造多版本数据块来实现读一致性。
         每个session查询时，如果对应的数据块发生改变，Oracle会在undo表空间中为这
      个session构造它查询时的旧的数据块。

         Mysql没有类似Oracle的构造多版本数据块的机制，只支持read commited的隔离级别。一
      个session读取数据时，其他session不能更改数据，但可以在表最后插入数据。
         session更新数据时，要加上排它锁，其他session无法访问数据。

      3）事务
         Oracle很早就完全支持事务。
         Mysql在InnoDB行级锁的情况下才支持事务。

      5）数据持久性
         Oracle提交的数据均可恢复，因为oracle把提交的SQL操作写入了在线联机日志文件中，保
      存到了磁盘上。如果出现数据库或主机异常重启，重启后Oracle可以靠联机在线日志恢复客户端
      提交的数据。

         Mysql会丢失数据。

      6）逻辑备份
         Oracle逻辑备份时不锁定数据，且备份的数据是一致的。
         Mysql逻辑备份时要锁定数据，才能保证备份的数据是一致的，影响业务正常的DML使用。

      7）复制
         oracle:既有推或拉式的传统数据复制，也有dataguard的双机或多机容灾机制，主库出现
      问题是，可以自动切换备库到主库，但配置管理较复杂。
         mysql:复制服务器配置简单，但主库出问题时，丛库有可能丢失一定的数据。且需要手工切
      换丛库到主库

      8）分区表和分区索引
         oracle的分区表和分区索引功能很成熟，可以提高用户访问db的体验。
         mysql的分区表还不太成熟稳定

      9）Oracle是大型数据库而Mysql是中小型数据库

      10）Oracle支持大并发，大访问量，是OLTP(On-Line Transaction Processing联机事务处理系统)最好的工具

      11）安装所用的空间差别也是很大的，Mysql安装完后才152M而Oracle有3G左右，且使用的时
      候Oracle占用特别大的内存空间和其他机器性能
</pre>

![](https://i.imgur.com/ZJ9S0Av.png)

<pre>
InnoDB存储引擎由多个内存块组成一个大的内存池。主要负责：
      1）维护所有进程/线程需要访问的多个内部数据结构
      2）缓存磁盘数据及修改过的数据，便于快速读取/写入（buffer/cache）
      3) 还有一部分重做日志（redo log buffer）


   InnoDB是多线程的模型，其后台包含多个线程，分别负责处理不同的任务。
      1：Master线程
        缓冲池数据异步刷新到磁盘
        保证数据的一致性（刷新脏页、合并插入缓冲、UNDO页回收）

      2.Io Thread
        为提高数据库写入性能，InnoDB大量采用AIO，此线程主要负责这些IO请求的回调处理，

        IO Thread 线程类型	       默认线程数
        insert buffer thread	   1
        log thread	               1
        innodb_read_io_threads	   4
        innodb_write_io_threads	   4

      3、Purge Thread
        事务被提交后，其所使用的undo log可能不再需要，因此需要Purge Thread来回收已经
      使用并分配的undo页
</pre>

<pre>
MyIsam为什么查询比InnoDB块
      
</pre>

<pre>
查看慢查询
      1）Springboot查看AOP拦截SQL接口
      2）Mysql配置慢查询日志打开
      3）Explain查询计划
</pre>

各种数据库中间件

![](https://i.imgur.com/zsj1kr3.png)

<pre>
关系型数据库 VS 非关系型数据库

      1）成本:NoSQL数据库简单容易部署，基本都是开源软件，不需要像oracle那样花费大量成本购买使用。
      2）查询速度:Nosql数据库将数据存储在缓存之中，关系型数据库将数据存储在磁盘上，NoSQL的
      查询速度远高于关系型数据库。
      3）存储数据的格式：NoSQL存储数据的格式是key,value形式，文档形式，图片形式，所以可以
      存储基础类型以及对象或者集合等各种形式，而数据库只支持基础类型。
      5）扩展性：关系型数据库又类似join这样的多表查询机制的限制导致扩展困难。
      6）NoSql维护的资料有限，因为Nosql是属于新的技术，不能和关系型数据库几十年的技术同日而语。
      7）Nosql不提供对SQL的支持，如果不支持sql这样的工业标准，将产生一定用户的学习和使用
      成本。
      8）Nosql不提供关系型数据库对事务的处理。
</pre>

<pre>
Mysql数据库的锁

      1）行锁（共享锁/排它锁）
      2) 表锁
      3）页级锁
      5）意向锁
      6）读锁
      7）写锁
      8）悲观锁
      9）乐观锁
</pre>

![](https://i.imgur.com/yl9rWW8.png)

<pre>
垂直切分的优点：
      1）数据库的拆分简单明了，拆分规则明确
      2）应用程序模块清晰明确，整合容易
      3）数据维护方便易行，容易定位

垂直切分的缺点：
      1）部分表关联无法在数据库级别完成，需要在程序中完成。
      2）对于访问极其频繁且数据量超大的表任然存在性能瓶颈，不一定能满足需求
      3）事务处理相对复杂
      5）切分达到一定程度后，扩展性会遇到瓶颈
      6）过分切分会带来系统过度复杂而难以维护

   针对于垂直切分可能遇到数据切分及事务问题，在数据库层面实在是很难找到一个较好的处理方案。
   实际应用案例中，数据库的垂直切分大多是与应用系统的模块相对 应，同一个模块的数据源存放于
   同一个数据库中，可以解决模块内部的数据关联问题。而模块与模块之间，则通过应用程序以服务接
   口方式来相互提供所需要的数 据。虽然这样做在数据库的总体操作次数方面确实会有所增加，但是在
   系统整体扩展性以及架构模块化方面，都是有益的。可能在某些操作的单次响应时间会稍有增 加，但
   是系统的整体性能很可能反而会有一定的提升。而扩展瓶颈问题，就只能依靠下一节将要介绍的数据
   水平切分架构来解决了。
</pre>

![](https://i.imgur.com/0nfB9DG.png)

<pre>
水平切分

    数据的垂直切分基本上可以简单的理解为按照表按照模块来切分数据，而水平切分就不再是 按照
    表或者是功能模块来切分了。一般来说，简单的水平切分主要是将某个访问极其平凡的表再按照某
    个字段的某种规则来分散到多个表之中，每个表中包含一部分 数据。

    简 单来说，我们可以将数据的水平切分理解为是按照数据行的切分，就是将表中的某些行切分到
    一个数据库，而另外的某些行又切分到其他的数据库中。当然，为了能 够比较容易的判定各行数
    据被切分到哪个数据库中了，切分总是都需要按照某种特定的规则来进行的。如根据某个数字类型
    字段基于特定数目取模，某个时间类型字 段的范围，或者是某个字符类型字段的hash值。如果整
    个系统中大部分核心表都可以通过某个字段来进行关联，那这个字段自然是一个进行水平分区的上
    上之选 了，当然，非常特殊无法使用就只能另选其他了。

水平切分的优点：
      1）表关联基本能够在数据库端完成
      2）不会存在某些大型数据量和高负载的表遇到性能瓶颈问题
      3）应用程序端整体架构改动相对较少
      5）事务处理相对简单
      6）只要切分规则能够定义好，基本上比较难遇到扩展性限制

水平切分的缺点：
      1）切分规则相对更为复杂，很难抽象出一个能够满足整个数据的切分规则
      2）后期数据的维护难度有所增加，认为手工定位数据更为困难
      3）应用系统个模块耦合难度较高，可能会对后面数据的迁移拆分造成一定的困难
</pre>

![](https://i.imgur.com/kNCV7Qt.png)

<pre>
水平切分 && 垂直切分的联合使用

      但是在实际的应用场 景中，除了那些负载并不是太大，业务逻辑也相对较简单的系统可以通过
      上面两种切分方法之一来解决扩展性问题之外，恐怕其他大部分业务逻辑稍微复杂一点，系负
      载大一些的系统，都无法通过上面任何一种数据的切分方法来实现较好的扩展性，而需要将上
      述两种切分方法结合使用，不同的场景使用不同的切分方法

联合切分的优点：
      1）可以充分利用垂直切分和水平切分各自的优势而避免各自的缺陷
      2）让系统扩展性得到最大化提升

联合切分的缺点：
      1）数据库系统架构比较复杂，维护难度更大
      2）应用程序架构也相对复杂
</pre>

<pre>
数据切分与整合可能带来的问题：
 
      1）引入分布式事务的问题

         在MySQL各个版本中，只有从MySQL5.0开始以后 的各个版本才开始对分布式事务提供支持，
         而且目前仅有Innodb提供分布式事务支持

         完全可以将一个跨多个数据库的分布式事务分拆成多个仅处于单个数据库上面的小事务，并通
         过应用程序来总控各个小事务。当然，这样作的要求就是我们的俄应用程序必须要有足够的健
         壮性，当然也会给应用程序带来一些技术难度。

      2）跨节点join的问题

         推荐通过应用程序来进行处理，先在驱动表所在的MySQLServer中取出相应的驱动结果集，
         然后根据驱动结果集再到被驱动表所在的 MySQLServer中取出相应的数据。可能很多读者朋
         友会认为这样做对性能会产生一定的影响，是的，确实是会对性能有一定的负面影响，但是除
         了此法， 基本上没有太多其他更好的解决办法了。而且，由于数据库通过较好的扩展之后，
         每台MySQLServer的负载就可以得到较好的控制，单纯针对单条 Query来说，其响应时间可
         能比不切分之前要提高一些，所以性能方面所带来的负面影响也并不是太大。更何况，类似于
         这种需要跨节点Join的需求也并不 是太多，相对于总体性能而言，可能也只是很小一部分而
         已。所以为了整体性能的考虑，偶尔牺牲那么一点点，其实是值得的，毕竟系统优化本身就是
         存在很多取舍 和平衡的过程

      3）跨节点合并分页排序问题
         解决的思路大体上和跨节点Join的解决类似，但是有一点和跨节点Join不太一样，Join很多
         时候都有一个驱动与被驱动的关系，所以Join 本身涉及到的多个表之间的数据读取一般都会
         存在一个顺序关系。但是排序分页就不太一样了，排序分页的数据源基本上可以说是一个表
         （或者一个结果集），本身 并不存在一个顺序关系，所以在从多个数据源取数据的过程是完
         全可以并行的。这样，排序分页数据的取数效率我们可以做的比跨库Join更高，所以带来的性
         能 损失相对的要更小，在有些情况下可能比在原来未进行数据切分的数据库中效率更高了。
         当然，不论是跨节点Join还是跨节点排序分页，都会使我们的应用服务 器消耗更多的资源，
         尤其是内存资源，因为我们在读取访问以及合并结果集的这个过程需要比原来处理更多的数据
</pre>