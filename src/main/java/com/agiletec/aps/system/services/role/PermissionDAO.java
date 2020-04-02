/*
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.agiletec.aps.system.services.role;

import com.agiletec.aps.system.common.AbstractDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Access Object per i permessi (Permission).
 *
 * @author M.Diana
 */
public class PermissionDAO extends AbstractDAO implements IPermissionDAO {

    private static final Logger _logger = LoggerFactory.getLogger(PermissionDAO.class);
    private static final String ALL_PERMISSIONS =
            "SELECT permissionname, descr FROM authpermissions ORDER BY permissionname";
    private static final String ADD_PERMISSION =
            "INSERT INTO authpermissions (permissionname, descr) VALUES ( ?, ? )";
    private static final String UPDATE_PERMISSIONS =
            "UPDATE authpermissions SET descr = ? WHERE permissionname = ? ";
    private static final String DELETE_PERMISSION =
            "DELETE FROM authpermissions WHERE permissionname = ? ";

    /**
     * Carica la mappa (indicizzata in base al nome) dei permessi di autorizzazione presenti nel db.
     *
     * @return La mappa dei permessi di autorizzazione.
     */
    public Map<String, Permission> loadPermissions() {
        Connection conn = null;
        Statement stat = null;
        ResultSet res = null;
        Map<String, Permission> permissions = new HashMap<String, Permission>();
        Permission permission = null;
        try {
            conn = this.getConnection();
            stat = conn.createStatement();
            res = stat.executeQuery(ALL_PERMISSIONS);
            while (res.next()) {
                permission = this.createPermissionFromRecord(res);
                permissions.put(permission.getName(), permission);
            }
        } catch (Throwable t) {
            _logger.error("Error loading permissions", t);
            throw new RuntimeException("Error loading permissions", t);
            //processDaoException(t, "Error loading permissions", "loadPermissions");
        } finally {
            closeDaoResources(res, stat, conn);
        }
        return permissions;
    }

    /**
     * Aggiunge un permesso di autorizzazione al db.
     *
     * @param permission Oggetto Permission rappresentante il permesso da aggiungere.
     */
    public void addPermission(Permission permission) {
        Connection conn = null;
        PreparedStatement stat = null;
        try {
            conn = this.getConnection();
            conn.setAutoCommit(false);
            stat = conn.prepareStatement(ADD_PERMISSION);
            stat.setString(1, permission.getName());
            stat.setString(2, permission.getDescription());
            stat.executeUpdate();
            conn.commit();
        } catch (Throwable t) {
            this.executeRollback(conn);
            _logger.error("Error while adding a permission", t);
            throw new RuntimeException("Error while adding a permission", t);
            //processDaoException(t, "Error while adding a permission", "addPermission");
        } finally {
            closeDaoResources(null, stat, conn);
        }
    }

    /**
     * Aggiorna un permesso nel db.
     *
     * @param permission Oggetto Permission rappresentante il permesso da aggionare.
     */
    public void updatePermission(Permission permission) {
        Connection conn = null;
        PreparedStatement stat = null;
        try {
            conn = this.getConnection();
            conn.setAutoCommit(false);
            stat = conn.prepareStatement(UPDATE_PERMISSIONS);
            stat.setString(1, permission.getDescription());
            stat.setString(2, permission.getName());
            stat.executeUpdate();
            conn.commit();
        } catch (Throwable t) {
            this.executeRollback(conn);
            _logger.error("Error while updating a permission", t);
            throw new RuntimeException("Error while updating a permission", t);
            //processDaoException(t, "Error while updating a permission", "updatePermission");
        } finally {
            closeDaoResources(null, stat, conn);
        }
    }

    /**
     * Cancella un permesso di autorizzazione dal db.
     *
     * @param permission Oggetto Permission rappresentante il permesso da eliminare.
     */
    public void deletePermission(Permission permission) {
        this.deletePermission(permission.getName());
    }

    /**
     * Cancella un permesso di autorizzazione dal db.
     *
     * @param permissionName Oggetto Permission rappresentante il permesso da eliminare.
     */
    public void deletePermission(String permissionName) {
        Connection conn = null;
        PreparedStatement stat = null;
        try {
            conn = this.getConnection();
            conn.setAutoCommit(false);
            stat = conn.prepareStatement(DELETE_PERMISSION);
            stat.setString(1, permissionName);
            stat.executeUpdate();
            conn.commit();
        } catch (Throwable t) {
            this.executeRollback(conn);
            _logger.error("Error while deleting a permission", t);
            throw new RuntimeException("Error while deleting a permission", t);
            //processDaoException(t, "Error while deleting a permission", "deletePermission");
        } finally {
            closeDaoResources(null, stat, conn);
        }
    }

    /**
     * Crea un permesso leggendo i valori dal record corrente del ResultSet passato. Attenzione: la query di origine del ResultSet deve
     * avere nella select list i campi esattamente in questo numero e ordine: 1=permissionname, 2=descr,
     *
     * @param res Il ResultSet da cui leggere i valori
     * @return L'oggetto permesso popolato.
     */
    private Permission createPermissionFromRecord(ResultSet res) throws SQLException {
        Permission permission = new Permission();
        permission.setName(res.getString(1));
        permission.setDescription(res.getString(2));
        return permission;
    }

}
