//package org.earlyspring.context;
//
//import com.web.config.AOPConfig;
//import com.web.config.Config;
//import com.web.entity.animal.Cat;
//import com.web.entity.animal.Lion;
//import com.web.entity.animal.Tiger;
//import com.web.entity.fruit.Fruit;
//import com.web.entity.human.Boy;
//import com.web.entity.human.Girl;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.jupiter.api.DisplayName;
//
///**
// * @author czf
// * @Date 2020/5/9 3:19 下午
// */
//public class AnnotationApplicationContextTest {
//
//    // 顶层容器
//    private AnnotationApplicationContext applicationContext = new AnnotationApplicationContext(Config.class, AOPConfig.class);
//
//    @Test
//    @DisplayName("对Component/Controller/Respository/Service的测试")
//    public void AnnotationTest1(){
//        // Component标注
//        Fruit apple = (Fruit) applicationContext.getBean("apple");
//        // Service标注
//        Fruit banana = (Fruit) applicationContext.getBean("banana");
//        // Respository标注
//        Fruit peach = (Fruit) applicationContext.getBean("peach");
//        // Controller标注
//        Fruit orange = (Fruit) applicationContext.getBean("orange");
//        Assert.assertTrue(apple.getName() == "apple");
//        Assert.assertTrue(banana.getName() == "banana");
//        Assert.assertTrue(peach.getName() == "peach");
//        Assert.assertTrue(orange.getName() == "orange");
//    }
//
//    @Test
//    @DisplayName("对ComponentScan/ComponentScans/Import的测试")
//    public void ScanTest(){
//        /**
//         * 解决了循环导入问题
//         */
//        // ComponentScan/ComponentScans
//        Assert.assertTrue(applicationContext.getBean("apple")!=null);
//        Assert.assertTrue(applicationContext.getBean("cat")!=null);
//        Assert.assertTrue(applicationContext.getBean("sakura")!=null);
//        // Import
//        Assert.assertTrue(applicationContext.getBean("log")!=null);
//    }
//
//    @Test
//    @DisplayName("对Autowired/Qualifier/Value的测试")
//    public void AutowiredTest(){
//        Cat cat = (Cat) applicationContext.getBean("cat");
//        Assert.assertTrue(cat.name.equals("cat"));
//        // Autowired + Qualifier
//        Assert.assertTrue(cat.getFavoriteFruit().getName().equals("apple"));
//        // Value
//        Assert.assertTrue(cat.getD1() == 1997);
//        Assert.assertTrue(cat.getF2() == 7.17f);
//        Assert.assertTrue(cat.getS3() == 12);
//        Assert.assertTrue(cat.getD4() == 19.00);
//    }
//
//
//    @Test
//    @DisplayName("对Lazy/Scope的测试")
//    public void AnnotationTest2(){
//        // 懒加载 + 多例
//        Assert.assertTrue(isLazy("Dog") && isPrototype("Dog"));
//
//        // 懒加载 + 单例子
//        Assert.assertTrue(isLazy("Monkey") && !isPrototype("Monkey"));
//
//        // 非懒加载 + 单例
//        Assert.assertTrue(!isLazy("Python") && !isPrototype("Python"));
//
//        // 非懒加载 + 多例 这一情况不存在， 因为只要是多例，一定是懒加载
//    }
//
//    /**
//     * 是否是多例
//     * @param beanName
//     * @return
//     */
//    private boolean isPrototype(String beanName) {
//        return applicationContext.getBean(beanName) != applicationContext.getBean(beanName);
//    }
//
//    /**
//     * 是否为懒惰加载
//     * @param beanName
//     * @return
//     */
//    private boolean isLazy(String beanName) {
//        return !applicationContext.containsSingleton(beanName);
//    }
//
//
//    @Test
//    @DisplayName("对AOP的测试(Aspect, Order)")
//    public void AOPTest(){
//        Cat cat = (Cat) applicationContext.getBean("cat");
//        // 正常say
//        cat.say();
//        System.out.println("===========================");
//        // 异常say
//        cat.saywithDivideZeroException();
//    }
//
//    @Test
//    @DisplayName("单例Bean的循环依赖测试")
//    public void LoopDependenceTest(){
//        Tiger tiger = (Tiger) applicationContext.getBean("tiger");
//        Lion lion = (Lion) applicationContext.getBean("lion");
//        Assert.assertTrue(tiger.getLion() == lion);
//        Assert.assertTrue(lion.getTiger() == tiger);
//    }
//
//    @Test
//    @DisplayName("动态代理的单例Bean的循环依赖测试")
//    public void LoopDependenceWithAOPTest(){
//        Boy xiaolang = (Boy) applicationContext.getBean("xiaolang");
//        Girl sakura = (Girl) applicationContext.getBean("sakura");
//        Assert.assertTrue(xiaolang.getGirl() == sakura);
//        Assert.assertTrue(sakura.getBoy() == xiaolang);
//    }
//
//}
