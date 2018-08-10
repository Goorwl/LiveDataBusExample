# 一、Android 消息总线简介

关于 `Android` 消息传递方式比较多，一般的系统原生实现方式比如 `Handler` 、自定义广播、接口回调，以及三方工具 `EventBus` 、`RxBus` 等，但是以上工具或多或少都有些缺点。偶然发现美团技术点评团队的一个消息通信工具，本文进行使用方式的简单介绍。

具体介绍参考原文：[Android消息总线的演进之路：用LiveDataBus替代RxBus、EventBus](https://tech.meituan.com/Android_LiveDataBus.html) 

# 二、推荐理由

肯定会有小伙伴问，既然有了成熟的 `EventBus` ，为什么要使用这个框架呢？我个人观点当前工具依赖于 `Android Architecture Components` 提出的框架 `LiveData` ，侵入性更小，依赖简单，使用方便，因此比较倾向使用此框架。

# 三、使用介绍

## 1、准备过程

当前框架依赖 `livedata` ，所以在 `module` 的 `gradle` 文件添加依赖：

    implementation "android.arch.lifecycle:livedata:1.1.1"

复制 [LiveDataBus.java](https://github.com/JeremyLiao/LiveDataBus/blob/master/LiveDataBus/livedatabus/src/main/java/com/jeremyliao/livedatabus/LiveDataBus.java) 文件到你项目内即可。

## 2、一般使用方式

### 订阅消息

    LiveDataBus.get()
            .with("key_word", String.class)					// 参数1：数据通信标识；参数2：消息类型
            .observe(this, new Observer<String>() {
                @Override
                public void onChanged(@Nullable String s) {
                    Toast.makeText(MainActivity.this, "get the first msg:" + s, Toast.LENGTH_SHORT).show();
                }
            });

### 发送消息

主线程发送：

    LiveDataBus
            .get()
            .with("key_word", String.class)
            .setValue("send the msg from MainThread!");

子线程发送消息：

	LiveDataBus.get()
            .with("key_word", String.class)
            .postValue("send the msg from SubThread!");

### 小结

由于LiveData 依赖于组件的生命周期，因此当绑定的组件不处于活跃期间，将无法触发消息实时更新，当界面一旦激活，则会立即触发更新行为。

当前方式使用，不需要考虑内存泄漏的问题，但是需要注意一点就是消息会覆盖，当订阅者处于非活跃状态，获取的信息是最新发送的一个消息，并不会重复获取，之前的消息会被丢弃。

## 3、跨生命周期使用方式

### 订阅消息

    private Observer<String> mObserver;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    
		mObserver = new Observer<String>() {
		    @Override
		    public void onChanged(@Nullable String s) {
		        Toast.makeText(MainActivity.this, "A-A : Forever get the msg is :" + s, Toast.LENGTH_SHORT).show();
		        Log.e(TAG, "A-A : Forever get the msg is :" + s);
		    }
		};
		
		//        测试跨界面消息发送
		LiveDataBus.get()
		        .with("key_forever", String.class)
		        .observeForever(mObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LiveDataBus.get().with("key_forever", String.class)
                .removeObserver(mObserver);
    }

和一般使用方式没啥区别，注意的就是要自己注销。

### 发送消息
主线程：

    LiveDataBus
            .get()
            .with("key_forever", String.class)
            .setValue("send the msg from A-B!");

子线程：

    LiveDataBus
			.get()
            .with("key_word", String.class)
            .postValue("send the msg from SubThread!");

### 小结

整体和一般使用方式没区别，主要在于订阅者需要使用的函数是 observeForever 替代了 observe ，还需要手动进行注销，不然容易出现内存泄漏的风险。

## 4、粘性事件使用方式

粘性事件，表示在之后注册的订阅行为也可以接收到之前的数据。

### 订阅消息

    LiveDataBus.get()
            .with("key_word", String.class)
            .observeSticky(Main2Activity.this, new Observer<String>() {
                @Override
                public void onChanged(@Nullable String s) {
                    Toast.makeText(Main2Activity.this, "A-B : Sticky get the msg is :" + s, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "A-B : Sticky get the msg is :" + s);
                }
            });

### 发送消息

主线程：

    LiveDataBus
            .get()
            .with("key_word", String.class)
            .setValue("send the msg from MainThread!");
子线程：

    LiveDataBus
			.get()
            .with("key_word", String.class)
            .postValue("send the msg from SubThread!");

### 小结

粘性事件也支持跨界面接受消息，当然也需要手动进行注销。更详细的使用方式参考：[LiveDataBus.README](https://github.com/JeremyLiao/LiveDataBus)

# 四、小结

经过以上使用，有没有觉得编码瞬间清爽一截，哈哈哈。

消息传递在Android程序中使用的还是比较多的，因此选择一个合适的方式还是比较重要的。

整体体验下来，还是推荐使用一般方式，毕竟在不活跃的时候进行数据更新会存在比较大的风险，当界面处于活跃状态再进行数据更新，并没有什么不妥。

但是感觉还有一个缺点就是注销的时候不是很方便，目前作者还在进行维护，未来可期。