package com.zgl.swsad.config;

public class Constants {

    /**
     * 存储当前登录用户id的字段名
     */
    public static final String CURRENT_USER_ID = "CURRENT_USER_ID";

    /**
     * token有效期（小时）
     */
    public static final int TOKEN_EXPIRES_HOUR = 72;

    /**
     * 存放Authorization的header字段
     */
    public static final String AUTHORIZATION = "authorization";

    /**
     *  存放图片的目录
     */
    public static final String IMAGEPATH = "images/";

    /**
     * sha256密钥
     */
    public static final String TOKEN_KEY= "swsad123456";

    public static final int INSERT_FAIL = -1;

    //User类
    //前两个是userType的类别，依次对应：用户、管理员
    // 后两个是sex的类别，依次对应为：男性、女性
    public static final int USER = 0;
    public static final int ADMINISTRATOR = 1;
    public static final int MALE = 0;
    public static final int FEMALE = 1;

    //Mission类
    //missionStatus的四个状态依次对应为：完成此任务的人数还不够、能接受此任务的人数已达上限、发布的任务过期、
    public static final int NEED_MORE_PEOPLE = 0;
    public static final int MAX_PEOPLE = 1;
    public static final int OVERDUE = 2;

    //Task类
    //taskSatus的四个状态，依次对应为：此任务未被人接收、此任务已被人接收且正在执行、此任务已被接收者执行完毕但发布者还未确认、此任务已被上方确认完成
    //taskType的两个类型，依次为：跑腿任务、问卷任务
    public static final int TO_DO = 0;
    public static final int DOING = 1;
    public static final int DONE_BUT_NOT_CONFIRM = 2;
    public static final int DONE_AND_CONFIRM = 3;
    public static final int TASK_ERRAND = 0;
    public static final int TASK_QUESTIONARE = 1;

    //Question类
    //questionType的类别，依次分别为：单选题、多选题、问答题
    public static final int SINGLE = 0;
    public static final int MULTIPLE = 1;
    public static final int QUESTION_AND_ANSWER = 2;

    //Report类
    //status的状态类别：mission没有被举报、mission被举报了
    public static final int NO_REPORTED = 0;
    public static final int REPORTED = 1;

    //Judge类
    //status的状态类别：task不需要仲裁、task需要仲裁
    public static final int NO_JUDGE = 0;
    public static final int JUDGING = 1;

    //定义日期格式
    public static final String DATA_FORMAT = "yyyy-MM-dd";

}
