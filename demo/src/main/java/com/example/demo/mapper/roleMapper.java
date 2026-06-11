package com.example.demo.mapper;

import com.example.demo.pojo.Power;
import com.example.demo.pojo.Role;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface roleMapper {

    @Select("SELECT \n" +
            "    r.role_id,\n" +
            "    r.role_name,\n" +
            "    r.updtime,\n" +
            "    GROUP_CONCAT(p.power_name ORDER BY p.power_name SEPARATOR ', ') AS powers\n" +
            "FROM \n" +
            "    role r\n" +
            "LEFT JOIN \n" +
            "    role_power rp ON r.role_id = rp.role_id\n" +
            "LEFT JOIN \n" +
            "    power p ON rp.power_id = p.power_id\n" +
            "GROUP BY \n" +
            "    r.role_id\n"+
            " ORDER BY\n" +
            "r.updtime DESC")
    List<Role> roles();
    @Select("select a.role_id,a.role_name from powersystem.role a,powersystem.user b,powersystem.user_role c where c.user_id=#{id} and a.role_id=c.role_id and b.user_id=c.user_id")
    List<Role> roleById(String id);

    void roleUpd(@Param("userId") String userId,@Param("roleIds") String roleIds);
    @Select("select power_id, power_name, fa_power_id,updtime from power order by updtime desc ")
    List<Power> allPower();
    @Select("select a.power_id,a.power_name,a.fa_power_id from power a,role_power b,role c where c.role_id=#{id} and a.power_id=b.power_id and c.role_id=b.role_id")
    List<Power> powerById(String id);

    void updRolePower(@Param("roleId") Integer roleId, @Param("powerIds") String powerIds);

    List<Role> quertRole(@Param("roleId") Integer roleId, @Param("roleName") String roleName);
    /*@Insert("insert into power (power_id, power_name, fa_power_id) values (#{powerId},#{powerName},#{faPowerId})")*/

    void addPower(Power power);
    @Select("CALL delete_roles_by_ids(#{roleIdList})")
    void deleteByIds(String roleIdList);

    void addRole(Role role);
    @Select("select * from role where role_id=#{id}")
    Role roleById1(Integer id);
    @Update("update role set role_name=#{roleName} ,updtime=now() where role_id=#{roleId}")
    void updRole(Role role);
    @Delete("delete from power where power_id=#{id}")
    void deletePower(Integer id);
    @Select("select power.power_name,power_id from power where power_id=#{id}")
    Power powerById1(Integer id);
    @Update("update power set power_name=#{powerName},updtime=now() where power_id=#{powerId}")
    void updPower(Power power);
}
