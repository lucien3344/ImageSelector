# ImageSelector  图片选择器


 用法如下：

 第一步： 在项目根目录下的build.gradle中添加如下代码：

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

 第二步：项目在app目录下的build.gradle中添加如下代码：

	dependencies {
	        implementation 'com.github.lucien3344:ImageSelector:1.0.2'
	}


 第三步：在项目AndroidManifest.xml的application中添加 如下属性：

     tools:replace="android:name"





 9宫格图片选择器 用法

   XML中

     <fragment
     android:id="@+id/gridImage_fragment"
     android:name="com.lucien3344.imageselector.ui.fragment.GridImageFragment"  // 9宫格
     android:layout_width="match_parent"
     android:layout_height="wrap_content"
     tools:layout="@layout/fragment_gridimage" />






  邮箱：lsh_2012@qq.com






