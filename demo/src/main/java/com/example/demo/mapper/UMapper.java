package com.example.demo.mapper;


import com.example.demo.pojo.Power;
import com.example.demo.pojo.U;
import com.example.demo.pojo.Uup;
import org.apache.ibatis.annotations.*;

import java.util.List;
@Mapper
public interface UMapper {
    @Select("SELECT \n" +
            "    u.user_id, \n" +
            "    u.user_name, \n" +
            "    u.image, \n" +
            "    u.gender, \n" +
            "    u.updtime, \n" +
            "    GROUP_CONCAT(r.role_name ORDER BY r.role_name SEPARATOR ', ') AS roles\n" +
            "FROM \n" +
            "    user u\n" +
            "LEFT JOIN \n" +
            "    user_role ur ON u.user_id = ur.user_id\n" +
            "LEFT JOIN \n" +
            "    role r ON ur.role_id = r.role_id\n" +
            "GROUP BY \n" +
            "    u.user_id " +
            "ORDER BY\n" +
            "u.updtime DESC")
    List<U> selectAllu();

    @Select("select * from powersystem.user where user_id=#{userId} and user.user_password=#{userPassword}")
    U getUByPswAndID(U u);


    void upu(Uup uup);
    @Select("select * from powersystem.user where user_id=#{id}")
    U selectById(String id);
   /* @Insert("insert into user(user_id,user_name,user_password,gender,image)" +
            "values (#{userId},#{userName},#{userPassword},#{gender},#{image})")*/
    void addUser(U u);

    List<U> ifUser(U u);
    @Update("CALL delete_users_by_ids(#{userIds})")
    void deleteByIds(@Param("userIds") String userIds);

    @Select("select * from power where power_id in (select b.power_id from role a,role_power b,user_role c where a.role_id=c.role_id and a.role_id=b.role_id and c.user_id=#{id} )")
    List<Power> userPower(Integer id);
    @Select("select DISTINCT a.user_id from user_role a,role_power b where a.user_id=#{id} and b.power_id=#{op} and a.role_id=b.role_id")
    U verify(String id, Integer op);
    @Select("select version from user where user_id=#{userId}")
    Integer version(String userId);
    @Delete("delete from powersystem.user where powersystem.user.user_id=#{id}")
    void deleteById(String id);
}

