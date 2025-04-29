package com.niuyin.tools.es;

import com.niuyin.tools.es.mapper.MemberMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * EsUserTest
 *
 * @AUTHOR: roydon
 * @DATE: 2024/10/11
 **/
@Slf4j
@SpringBootTest
//@RunWith(SpringRunner.class)
public class EsUserTest {

//    @Autowired
//    private RestHighLevelClient restHighLevelClient;

    @Resource
    private MemberMapper memberMapper;

//    @Test
//    @DisplayName("初始化用户数据")
//    public void initUser() throws IOException {
//        // 1.0 查询全部用户
//        List<Member> memberList = memberMapper.searchExistUserList();
//        List<UserEO> userEOS = new ArrayList<>();
//        memberList.forEach(item -> {
//            UserEO userEO = BeanCopyUtils.copyBean(item, UserEO.class);
//            userEO.setUserId(item.getUserId().toString());
//            userEO.setUsername(item.getUserName());
//            userEO.setCreateTime(LocalDateTimeUtils.localDateTime2Date(item.getCreateTime()));
//            userEOS.add(userEO);
//        });
//        // 1.1 批量保存到es
//        BulkRequest bulkRequest = new BulkRequest("doc_user");
//        for (UserEO vo : userEOS) {
//            IndexRequest indexRequest = new IndexRequest().id(vo.getUserId()).source(JSON.toJSONString(vo), XContentType.JSON);
//            //批量添加数据
//            bulkRequest.add(indexRequest);
//        }
//        restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
//    }
}
