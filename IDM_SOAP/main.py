from spyne import ServiceBase, rpc, Application, String, Iterable,Integer
from spyne.protocol.soap import Soap11
from spyne.server.wsgi import WsgiApplication
from repositories import user_repository, role_repository, users_roles_repository
from json import dumps

class IDM_SOAP_Service(ServiceBase):
###########################  USERS  ###########################
    @rpc(String, String, _returns=String)
    def create_user(ctx, username, password):
        return user_repository.create_user(username, password)

    @rpc(_returns=Iterable(String))
    def get_usersnames(ctx):
        users = user_repository.get_users()
        if len(users) == 0:
            yield "There are no entries in the users table."
        for user in users:
            yield user.username

    @rpc(Integer, _returns = String)
    def get_user_by_id(ctx, id):
        user = user_repository.get_user_by_id(id)
        if user is None:
            return "Specified user does not exist!"
        roles = ', '.join(role.value for role in user.roles)
        return f"id={user.id}, username={user.username}, password={user.password}, roles = {roles}"

    @rpc(String, _returns = String)
    def get_user_by_username(ctx, username):
        user = user_repository.get_user_by_username(username)
        response = {
            "id": user.id,
            "username": user.username,
            "password": user.password
        }
        if user is None:
            return "Specified user does not exist!"
        #return f"id={user.id} username={user.username} password={user.password}"
        return dumps(response)

    @rpc(String, _returns=String)
    def delete_user_by_username(ctx, username):
        return user_repository.delete_user_by_username(username)

    @rpc(Integer, String, _returns=String)
    def update_username_for_user(ctx, id, updated_name):
        return user_repository.update_username_for_user(id, updated_name)

    @rpc(Integer, String, _returns=String)
    def update_password_for_user(ctx, id, updated_password):
        return user_repository.update_password_for_user(id, updated_password)

    @rpc(String, String, _returns=String)
    def check_login(ctx, username, password):
        user = user_repository.get_user_by_username(username)
        if user == None:
            return "Incorrect login details!"

        if (user.password == password):
            return "Successfully logged in!"

        return "Incorrect password!"

    @rpc(_returns=Iterable(String))
    def get_all_users(ctx):
        users = user_repository.get_all_users_info()
        if len(users) == 0:
            return "NO USERS!"
        return users

    @rpc(Integer, Integer, _returns=String)
    def check_user_role_association(ctx, user_id, role_id):
        user = user_repository.get_user_by_id(user_id)
        if user == None:
            return "User doesn't exist!"
        roles = list(users_roles_repository.get_user_roles(user.id))
        for current_role in roles:
            if role_id == current_role:
                return "Successful authorization! The user has access! "
        return "Authorization failed! Access Denied!"

    @rpc(Integer, _returns=String)
    def get_user_info(ctx, user_id):
        data = user_repository.get_user_info(user_id)
        return data

###########################  ROLES  ###########################
    @rpc(_returns=Iterable(String))
    def get_roles(ctx):
        roles = role_repository.get_roles()
        if len(roles) == 0:
            yield "There are no entries in the users table"
        for role in roles:
            yield role.value

    @rpc(Integer, _returns=String)
    def get_role_by_id(ctx, id):
        role = role_repository.get_role_by_id(id)
        if role is None:
            return "Specified role does not exist!"
        return f"id={role.id}, role={role.value} "

###########################  USERS_ROLES  ###########################

    @rpc(Integer, Integer, _returns=String)
    def add_role_for_user(ctx, user_id, role_id):
        return users_roles_repository.add_role_for_user(user_id, role_id)


    @rpc(Integer, _returns=Iterable(String))
    def get_user_roles(ctx, user_id):
        roles = list(users_roles_repository.get_user_roles(user_id))
        return roles


application = Application([IDM_SOAP_Service], 'services.IDM.soap',
                          in_protocol=Soap11(validator='lxml'),
                          out_protocol=Soap11())

wsgi_application = WsgiApplication(application)
if __name__ == '__main__':
    import logging

    from wsgiref.simple_server import make_server

    logging.basicConfig(level=logging.INFO)
    logging.getLogger('spyne.protocol.xml').setLevel(logging.INFO)

    logging.info("listening to http://127.0.0.1:8000")
    logging.info("wsdl is at: http://127.0.0.1:8000/?wsdl")

    server = make_server('127.0.0.1', 8000, wsgi_application)
    server.serve_forever()


