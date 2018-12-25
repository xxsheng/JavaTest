package cn.dao;

import cn.pojo.Tb_user;
import cn.pojo.Tb_userExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface Tb_userMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_user
     *
     * @mbggenerated
     */
    int countByExample(Tb_userExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_user
     *
     * @mbggenerated
     */
    int deleteByExample(Tb_userExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_user
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer Id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_user
     *
     * @mbggenerated
     */
    int insert(Tb_user record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_user
     *
     * @mbggenerated
     */
    int insertSelective(Tb_user record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_user
     *
     * @mbggenerated
     */
    List<Tb_user> selectByExample(Tb_userExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_user
     *
     * @mbggenerated
     */
    Tb_user selectByPrimaryKey(Integer Id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_user
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") Tb_user record, @Param("example") Tb_userExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_user
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") Tb_user record, @Param("example") Tb_userExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_user
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(Tb_user record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_user
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(Tb_user record);
}