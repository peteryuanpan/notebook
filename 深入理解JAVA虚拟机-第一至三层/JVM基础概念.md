- [JVM基础概念](#JVM基础概念)
  - [JAVA运行时环境逻辑图](#JAVA运行时环境逻辑图)
  - [OOP-KLASS模型](#OOP-KLASS模型)
  - [InstanceKlass](#InstanceKlass)
  - [InstanceMirrorKlass](#InstanceMirrorKlass)
  - [ArrayKlass和TypeArrayKlass和ObjArrayKlass](#ArrayKlass和TypeArrayKlass和ObjArrayKlass)
  - [InstanceRefKlass](#InstanceRefKlass)

# JVM基础概念

### JAVA运行时环境逻辑图

![image](http://tswork.peterpy.cn/java_runtime.png)

### OOP-KLASS模型

OOP-KLASS模型是JVM底层的数据结构，理解JVM的必要概念
- Klass是Java类在JVM中的存在形式
- OOP是JAVA对象在JVM中的存在形式
- InstanceKlass是类的元信息
- InstanceMirrorKlass是类的Class对象
- ArrayKlass表示的是数组类的元信息
- TypeArrayKlass表示基本数组类的元信息
- ObjArrayKlass表示引用数组类的元信息

下面是这几个Klass的继承关系图

![image](https://user-images.githubusercontent.com/10209135/89729486-ced8f380-da68-11ea-81d4-e4b19825a4a0.png)

来看一下openjdk8源码

### InstanceKlass

https://github.com/peteryuanpan/openjdk-8u40-source-code-mirror/blob/master/hotspot/src/share/vm/oops/instanceKlass.hpp#L138

```cpp
// An InstanceKlass is the VM level representation of a Java class.
// It contains all information needed for at class at execution runtime.

//  InstanceKlass layout:
//    [C++ vtbl pointer           ] Klass
//    [subtype cache              ] Klass
//    [instance size              ] Klass
//    [java mirror                ] Klass
//    [super                      ] Klass
//    [access_flags               ] Klass
//    [name                       ] Klass
//    [first subklass             ] Klass
//    [next sibling               ] Klass
//    [array klasses              ]
//    [methods                    ]
//    [local interfaces           ]
//    [transitive interfaces      ]
//    [fields                     ]
//    [constants                  ]
//    [class loader               ]
//    [source file name           ]
//    [inner classes              ]
//    [static field size          ]
//    [nonstatic field size       ]
//    [static oop fields size     ]
//    [nonstatic oop maps size    ]
//    [has finalize method        ]
//    [deoptimization mark bit    ]
//    [initialization state       ]
//    [initializing thread        ]
//    [Java vtable length         ]
//    [oop map cache (stack maps) ]
//    [EMBEDDED Java vtable             ] size in words = vtable_len
//    [EMBEDDED nonstatic oop-map blocks] size in words = nonstatic_oop_map_size
//      The embedded nonstatic oop-map blocks are short pairs (offset, length)
//      indicating where oops are located in instances of this klass.
//    [EMBEDDED implementor of the interface] only exist for interface
//    [EMBEDDED host klass        ] only exist for an anonymous class (JSR 292 enabled)

class InstanceKlass: public Klass {
  friend class VMStructs;
  friend class ClassFileParser;
  friend class CompileReplay;

 protected:
  // Constructor
  InstanceKlass(int vtable_len,
                int itable_len,
                int static_field_size,
                int nonstatic_oop_map_size,
                ReferenceType rt,
                AccessFlags access_flags,
                bool is_anonymous);
...
 protected:
  // Annotations for this class
  Annotations*    _annotations;
  // Array classes holding elements of this class.
  Klass*          _array_klasses;
  // Constant pool for this class.
  ConstantPool* _constants;
  // The InnerClasses attribute and EnclosingMethod attribute. The
  // _inner_classes is an array of shorts. If the class has InnerClasses
  // attribute, then the _inner_classes array begins with 4-tuples of shorts
  // [inner_class_info_index, outer_class_info_index,
  // inner_name_index, inner_class_access_flags] for the InnerClasses
  // attribute. If the EnclosingMethod attribute exists, it occupies the
  // last two shorts [class_index, method_index] of the array. If only
  // the InnerClasses attribute exists, the _inner_classes array length is
  // number_of_inner_classes * 4. If the class has both InnerClasses
  // and EnclosingMethod attributes the _inner_classes array length is
  // number_of_inner_classes * 4 + enclosing_method_attribute_size.
  Array<jushort>* _inner_classes;

  // the source debug extension for this klass, NULL if not specified.
  // Specified as UTF-8 string without terminating zero byte in the classfile,
  // it is stored in the instanceklass as a NULL-terminated UTF-8 string
  char*           _source_debug_extension;
  // Array name derived from this class which needs unreferencing
  // if this class is unloaded.
  Symbol*         _array_name;

  // Number of heapOopSize words used by non-static fields in this klass
  // (including inherited fields but after header_size()).
  int             _nonstatic_field_size;
  int             _static_field_size;    // number words used by static fields (oop and non-oop) in this klass
  // Constant pool index to the utf8 entry of the Generic signature,
  // or 0 if none.
  u2              _generic_signature_index;
  // Constant pool index to the utf8 entry for the name of source file
  // containing this klass, 0 if not specified.
  u2              _source_file_name_index;
  u2              _static_oop_field_count;// number of static oop fields in this klass
  u2              _java_fields_count;    // The number of declared Java fields
  int             _nonstatic_oop_map_size;// size in words of nonstatic oop map blocks

  // _is_marked_dependent can be set concurrently, thus cannot be part of the
  // _misc_flags.
  bool            _is_marked_dependent;  // used for marking during flushing and deoptimization
  bool            _has_unloaded_dependent;

  enum {
    _misc_rewritten                = 1 << 0, // methods rewritten.
    _misc_has_nonstatic_fields     = 1 << 1, // for sizing with UseCompressedOops
    _misc_should_verify_class      = 1 << 2, // allow caching of preverification
    _misc_is_anonymous             = 1 << 3, // has embedded _host_klass field
    _misc_is_contended             = 1 << 4, // marked with contended annotation
    _misc_has_default_methods      = 1 << 5, // class/superclass/implemented interfaces has default methods
    _misc_declares_default_methods = 1 << 6  // directly declares default methods (any access)
  };
  u2              _misc_flags;
  u2              _minor_version;        // minor version number of class file
  u2              _major_version;        // major version number of class file
  Thread*         _init_thread;          // Pointer to current thread doing initialization (to handle recusive initialization)
  int             _vtable_len;           // length of Java vtable (in words)
  int             _itable_len;           // length of Java itable (in words)
  OopMapCache*    volatile _oop_map_cache;   // OopMapCache for all methods in the klass (allocated lazily)
  MemberNameTable* _member_names;        // Member names
  JNIid*          _jni_ids;              // First JNI identifier for static fields in this class
  jmethodID*      _methods_jmethod_ids;  // jmethodIDs corresponding to method_idnum, or NULL if none
  nmethodBucket*  _dependencies;         // list of dependent nmethods
  nmethod*        _osr_nmethods_head;    // Head of list of on-stack replacement nmethods for this class
  BreakpointInfo* _breakpoints;          // bpt lists, managed by Method*
  // Array of interesting part(s) of the previous version(s) of this
  // InstanceKlass. See PreviousVersionWalker below.
  GrowableArray<PreviousVersionNode *>* _previous_versions;
  // JVMTI fields can be moved to their own structure - see 6315920
  // JVMTI: cached class file, before retransformable agent modified it in CFLH
  JvmtiCachedClassFileData* _cached_class_file;

  volatile u2     _idnum_allocated_count;         // JNI/JVMTI: increments with the addition of methods, old ids don't change

  // Class states are defined as ClassState (see above).
  // Place the _init_state here to utilize the unused 2-byte after
  // _idnum_allocated_count.
  u1              _init_state;                    // state of class
  u1              _reference_type;                // reference type

  JvmtiCachedClassFieldMap* _jvmti_cached_class_field_map;  // JVMTI: used during heap iteration

  NOT_PRODUCT(int _verify_count;)  // to avoid redundant verifies

  // Method array.
  Array<Method*>* _methods;
  // Default Method Array, concrete methods inherited from interfaces
  Array<Method*>* _default_methods;
  // Interface (Klass*s) this class declares locally to implement.
  Array<Klass*>* _local_interfaces;
  // Interface (Klass*s) this class implements transitively.
  Array<Klass*>* _transitive_interfaces;
  // Int array containing the original order of method in the class file (for JVMTI).
  Array<int>*     _method_ordering;
  // Int array containing the vtable_indices for default_methods
  // offset matches _default_methods offset
  Array<int>*     _default_vtable_indices;

  // Instance and static variable information, starts with 6-tuples of shorts
  // [access, name index, sig index, initval index, low_offset, high_offset]
  // for all fields, followed by the generic signature data at the end of
  // the array. Only fields with generic signature attributes have the generic
  // signature data set in the array. The fields array looks like following:
  //
  // f1: [access, name index, sig index, initial value index, low_offset, high_offset]
  // f2: [access, name index, sig index, initial value index, low_offset, high_offset]
  //      ...
  // fn: [access, name index, sig index, initial value index, low_offset, high_offset]
  //     [generic signature index]
  //     [generic signature index]
  //     ...
  Array<u2>*      _fields;
```

这里贴一下这些成员变量的中文解释
```text
_annotations：保存该类的所有注解
_array_klasses：保存数组元素所关联的klass指针
_constants：保存该类的常量池指针
_inner_classes：保存内部类相关的信息
_array_name：如果该类是数组，就会生成数组类名词，如“[Ljava/lang/String;”
_nonstatic_field_size：非静态字段数量
_static_field_size：静态字段数量
_generic_signature_index：泛型签名在常量池中的索引
_source_file_name_index：文件名在常量池中的索引
_static_oop_field_count：该类包含的静态的引用类型字段个数
_java_fields_count：已声明的Java字段数量
_nonstatic_oop_map_size：非静态oop映射块的大小(以字为单位)
_is_marked_dependent：用于刷新和反优化期间打标
_minor_version：主版本号
_major_version：次版本号
_init_thread：初始化此类的线程
_vtable_len：虚函数表的大小
_itable_len：接口函数表的大小
_oop_map_cache：该类所有方法的OopMapCache(延迟分配)
_member_names：MemberNameTable指针
_jni_ids：存放jni_id单向链表的首地址（什么是jni_id?）
_methods_jmethod_ids：与method_idnum对应的jmethodIDs，如果没有，则为NULL
_dependencies：存放nmethod的Bucket的首地址
_osr_nmethods_head：栈上替换nmethods的链表的首地址
_breakpoints：断点链表首地址
_previous_versions：此实例的前一个版本的有趣部分的数组。请参见下面的PreviousVersionWalker
_cached_class_file：缓存的类文件
_idnum_allocated_count：已经分配的idnum的个数
_init_state：该类的状态，值：allocated（已分配内存但未链接）、loaded（加载并插入到类层次结构中但仍未链接）、linked（验证及链接成功但未初始化）、being_initialized（正在初始化）、fully_initialized（已完成初始化）、initialization_error（初始化出错）
_reference_type：引用类型
_methods：存储该类的所有方法对象的指针的数组指针
_default_methods：存储从接口继承的所有方法对象的指针的数组指针
_local_interfaces：数组指针，存储所有实现的接口的指针
_transitive_interfaces：数组，存储直接实现的接口指针+接口间继承实现的接口指针
_method_ordering：包含类文件中方法的原始顺序的Int数组，JVMTI需要用到
_default_vtable_indices：默认构造方法在虚表中的索引
_fields：类的成员属性
```

### InstanceMirrorKlass

https://github.com/peteryuanpan/openjdk-8u40-source-code-mirror/blob/master/hotspot/src/share/vm/oops/instanceMirrorKlass.hpp#L41

```cpp
// An InstanceMirrorKlass is a specialized InstanceKlass for
// java.lang.Class instances.  These instances are special because
// they contain the static fields of the class in addition to the
// normal fields of Class.  This means they are variable sized
// instances and need special logic for computing their size and for
// iteration of their oops.

class InstanceMirrorKlass: public InstanceKlass {
  friend class VMStructs;
  friend class InstanceKlass;

 private:
  static int _offset_of_static_fields;

  // Constructor
  InstanceMirrorKlass(int vtable_len, int itable_len, int static_field_size, int nonstatic_oop_map_size, ReferenceType rt, AccessFlags access_flags,  bool is_anonymous)
    : InstanceKlass(vtable_len, itable_len, static_field_size, nonstatic_oop_map_size, rt, access_flags, is_anonymous) {}

 public:
  InstanceMirrorKlass() { assert(DumpSharedSpaces || UseSharedSpaces, "only for CDS"); }
  // Type testing
  bool oop_is_instanceMirror() const             { return true; }
```

**InstanceKlass和InstanceMirrorKlass**

可以看出来
- InstanceKlass中定义了Java运行时环境类所需的所有数据信息，比如 constants 常量池、methods 方法 等（An InstanceKlass is the VM level representation of a Java class. It contains all information needed for at class at execution runtime.）
- InstanceMirrorKlass是InstanceKlass的一个子类
- InstanceMirrorKlass是java.lang.Class类专用的InstanceKlass（An InstanceMirrorKlass is a specialized InstanceKlass for java.lang.Class instances.）

简单总结一下，InstanceKlass包含了Java运行时环境中类所需的所有数据信息，在类加载这一步，类加载器会将.class文件读入类加载器的class content，然后以InstanceKlass的形式写入JVM内存区域的方法区中，而InstanceMirrorKlass是类所对应的Class对象（java.lang.Class）的InstanceKlass

> 辗转反侧了很久，终于理解了InstanceKlass和InstanceMirrorKlass

> 对于InstanceKlass，在Java中，对于所有类，类的内部属性轮廓是不是可以认为是一样的？类都有父类、实现接口、变量、方法、代码块等，而变量和方法也有属性，即可访问性（public、private、protected、default）、静态与非静态，还有数组形式的。无论如何，类的内部属性以及属性的属性，是有限的、可列举的，那么在JVM中以C++代码就可以用一个类来表示，它就是InstanceKlass，这一层是比较好理解的

> 对于InstanceMirrorKlass的理解，首先需要理解什么是java.lang.Class，我们来看一下这个类的注释吧：Instances of the class Class represent classes and interfaces in a running Java application. 翻译过来说，就是类或接口在Java运行时环境中的一个表达方式，再看看java.lang.Class的方法就知道，getConstructors()、getMethods()、getFields()、getDeclaredFields()，这些都是类的内部属性，且对于任何类来说，都可以用形如A.class的方法来获取java.lang.Class，即每个Java类都有一个java.lang.Class，那么在JVM中以C++代码表示，每个类的java.lang.Class就是InstanceMirrorKlass。java.lang.Class也是一个类，InstanceMirrorKlass是InstanceKlass的子类

**2020-10补充之Class对象**

参考 [启明南-JVM底层klass体系](https://mp.weixin.qq.com/s/M1LPZWX-vhtYA5uD6gWHLA)

- InstanceKlass表示类的元信息，是类加载过程中，将Class元信息读入内存并转化而来的C++类，存储在方法区中
- InstanceMirrorKlass表示类的Class对象，它表示镜像类，还存储了类的静态变量信息，存储在堆区中

关于Class对象，JVM并没有将描述Java类元信息的instanceKlass直接暴露给Java程序使用，而是又抽象了一层，即所谓的镜像类：instanceMirrorKlass。jdk8以后，类的静态属性也由存储在instanceKlass实例中转为存放在镜像类的实例中。详情见源码

https://github.com/peteryuanpan/openjdk-8u40-source-code-mirror/blob/master/hotspot/src/share/vm/classfile/classFileParser.cpp#L3696

来看一下parseClassFile方法
```cpp
instanceKlassHandle ClassFileParser::parseClassFile(Symbol* name,
                                                    ClassLoaderData* loader_data,
                                                    Handle protection_domain,
                                                    KlassHandle host_klass,
                                                    GrowableArray<Handle>* cp_patches,
                                                    TempNewSymbol& parsed_name,
                                                    bool verify,
                                                    TRAPS) {
……
    // Allocate mirror and initialize static fields
    java_lang_Class::create_mirror(this_klass, protection_domain, CHECK_(nullHandle));
……  
```

来看一下create_mirror方法，第六行代码：初始化静态字段do_local_static_fields
```cpp
oop java_lang_Class::create_mirror(KlassHandle k, Handle protection_domain, TRAPS) {
……
    Handle mirror = InstanceMirrorKlass::cast(SystemDictionary::Class_klass())->allocate_instance(k, CHECK_0);
……
      // Initialize static fields
      InstanceKlass::cast(k())->do_local_static_fields(&initialize_static_field, CHECK_NULL);
……
```

来看一下allocate_instance方法，从代码中可以看出来，是从堆区分配内存的（CollectedHeap::Class_obj_allocate），所以说Class对象是分配在堆上的
```cpp
instanceOop InstanceMirrorKlass::allocate_instance(KlassHandle k, TRAPS) {
  // Query before forming handle.
  int size = instance_size(k);
  KlassHandle h_k(THREAD, this);
  instanceOop i = (instanceOop) CollectedHeap::Class_obj_allocate(h_k, size, k, CHECK_NULL);
  return i;
}
```

总结一下，类加载器将.class文件载入JVM中，parse后生成的是instanceKlass对象，之后会生成这个Klass类对应的镜像类实例，并将Java类中的静态变量初始化后存储在镜像类实例中，这个镜像类就是Java代码中的Class对象

### ArrayKlass和TypeArrayKlass和ObjArrayKlass

在理解了InstanceKlass后，这三个类就很好理解了

- ArrayKlass表示的是数组类的元信息（ArrayKlass is the abstract baseclass for all array classes）
- TypeArrayKlass表示基本数组类的元信息（A TypeArrayKlass is the klass of a typeArray, It contains the type and size of the elements）
- ObjArrayKlass表示引用数组类的元信息（ObjArrayKlass is the klass for objArrays）

下面的源码部分

https://github.com/peteryuanpan/openjdk-8u40-source-code-mirror/blob/master/hotspot/src/share/vm/oops/arrayKlass.hpp
```cpp
// ArrayKlass is the abstract baseclass for all array classes

class ArrayKlass: public Klass {
  friend class VMStructs;
 private:
  int      _dimension;         // This is n'th-dimensional array.
  Klass* volatile _higher_dimension;  // Refers the (n+1)'th-dimensional array (if present).
  Klass* volatile _lower_dimension;   // Refers the (n-1)'th-dimensional array (if present).
  int      _vtable_len;        // size of vtable for this klass
  oop      _component_mirror;  // component type, as a java/lang/Cl
```

https://github.com/peteryuanpan/openjdk-8u40-source-code-mirror/blob/master/hotspot/src/share/vm/oops/typeArrayKlass.hpp
```cpp
// A TypeArrayKlass is the klass of a typeArray
// It contains the type and size of the elements

class TypeArrayKlass : public ArrayKlass {
  friend class VMStructs;
 private:
  jint _max_length;            // maximum number of elements allowed in an array

  // Constructor
  TypeArrayKlass(BasicType type, Symbol* name);
  static TypeArrayKlass* allocate(ClassLoaderData* loader_data, BasicType type, Symbol* name, TRAPS);
 public:
  TypeArrayKlass() {} // For dummy objects.
```

https://github.com/peteryuanpan/openjdk-8u40-source-code-mirror/blob/master/hotspot/src/share/vm/oops/objArrayKlass.hpp
```cpp
// ObjArrayKlass is the klass for objArrays

class ObjArrayKlass : public ArrayKlass {
  friend class VMStructs;
 private:
  Klass* _element_klass;            // The klass of the elements of this array type
  Klass* _bottom_klass;             // The one-dimensional type (InstanceKlass or TypeArrayKlass)

  // Constructor
  ObjArrayKlass(int n, KlassHandle element_klass, Symbol* name);
  static ObjArrayKlass* allocate(ClassLoaderData* loader_data, int n, KlassHandle klass_handle, Symbol* name, TRAPS);
 public:
  // For dummy objects
  ObjArrayKlass() {}
```

### InstanceRefKlass

描述java.lang.ref.Reference的子类，这部分的概念与强软弱虚引用、垃圾回收有关系，见后续章节
