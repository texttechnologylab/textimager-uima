package org.hucompute.services.uima.database.neo4j.data;


import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import java.util.Set;

/**
 * The MasterDataBase (MDB) represents a session on the database. It offers access to transaction handling,
 * permission handling and factories for objects.
 * <p/>
 * In most cases the user of the API will not have to care for transactions, especially when writing a CommandProcessor:
 * When the CommandProcessor gets control, an MDB ist already instantiated by the CommandDispatcher with an opened
 * transaction. In case of an error the rollback is automatically handled by the CommandDispatcher. In no Exception
 * occurs, the CommandDispatcher will commit the transaction. There are however cases when special care needs to be taken.
 * When you want to access the MDB from a Thread, the MDB still holds a transaction from the initial thread.
 * Attempting to use this thread for writes will cause a NotInTransaction Exception. The safe procedure is as follows:
 * <code><pre>
 *   cd.setThreadMode(true); // Will skip the automatic close() call in the cd after you CommandProcessor finished
 *   cd.getMdb().success(); // Mark current transaction successfull
 *   cd.getMdb().finish(); // Finish current transaction
 *   myThread.start()
 *   ...
 * </pre></code>
 * Within your thread:
 * <code><pre>
 *   cd.begin();
 *   ...
 *   cd.close(); // Close CommandDispatcher. Wills also close transaction
 * </pre></code>
 * If the code in your thread failed issue a cd.failure(), then finally call cd.close().
 * <p/>
 * An MDB instance is always assiciated with a user of the system. All public methods are executed from the
 * perspective of that user. For example getting all Documents via the MDFactory will only fetch those to which the
 * user has at leat read access. So in general you can assume that when you have an instance of a class, you also
 * have read permissions on that. You can use the API without worrying (too much ;-) about permissions. The public
 * methods will throw an Exception when you try something that the current MDB user is not allowed to.
 * <p/>
 * There are cases when certain insecurities cannot be avoided. That is: If you really want you can get more from the
 * database than the current MDB user is actually allowed to. In short: Do not move the entire responsibility for
 * security of data to the API and try to avoid security holes when implementing CommandProcessors. Since we have
 * sensible data in our System we need to take care that the data is only available to those you have proper permissions.
 */
public interface MDB {

    void setSession(String session);

    String getSession();

    /**
     * Trigger a live backup of the MDB. Normal API users will not need this- and should not use it ;-).
     *
     * @return true iff the backup was successful
     */
    boolean backup();

    Node getNodeById(long lID);

    Relationship getRelationShipById(long lID);


    Node findNode(Label label, String sProperty, Object object);

    Set<Node> getNodes(Label label, String sProperty, Object object);

    Set<Node> getNodes(Label label);

    void createIndex(Label pLabel, String ptype);

}
