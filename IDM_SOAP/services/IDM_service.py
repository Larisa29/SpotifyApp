import spyne.model.fault
from spyne import ServiceBase, rpc, String, Iterable,Integer

from base.sql_base import Session
from enums.roles_types import Roles
from models.dto.roleDTO import RoleDTO
from models.dto.user_dto import UserDTO
from repositories import user_repository, role_repository, users_roles_repository
from repositories.role_repository import get_role_by_name
from validation.password_validation import check_password_is_valid, encode_password, password_match


class IDM_SOAP_Service(ServiceBase):

###########################  USERS  ###########################
    @rpc(String, String, _returns=UserDTO)
    def create_user(ctx, username, password): #all the users are clients upon registering
        role = get_role_by_name(Roles.CLIENT.name)

        if not username:
            raise spyne.model.fault.Fault(faultcode='Client', faultstring='Invalid request: USERNAME is missing')
        if not password:
            raise spyne.model.fault.Fault(faultcode='Client', faultstring='Invalid request: PASSWORD is missing')
        # if role.value is None:
        #     raise spyne.model.fault.Fault(faultcode='Client', faultstring='Invalid request: ROLE is missing')
        # if role.value not in [Roles.CONTENT_MANAGER.name, Roles.APP_ADMIN.name, Roles.ARTIST.name, Roles.CLIENT.name]:
        #     raise spyne.model.fault.Fault(faultcode='Client', faultstring='Invalid request: Invalid ROLE')
        if not check_password_is_valid(password):
            raise(spyne.model.fault.Fault(faultcode='Client', faultstring='Invalid Request: Password is too weak.'))

        encoded_password = encode_password(password)

        return user_repository.create_user(username, encoded_password, role)
    @rpc(_returns=Iterable(String))
    def get_usernames(ctx):
        users = user_repository.get_users()
        if len(users) == 0:
            yield "There are no entries in the users table."
        for user in users:
            yield user.username

    @rpc(Integer, _returns = UserDTO)
    def get_user_by_id(ctx, id):
        user = user_repository.get_user_by_id(id)
        if user is None:
            raise spyne.model.fault.Fault(faultcode='Client', faultstring='Specified user does not exist!')

        #create a list of roles, each role having RoleDTO type
        roles = [RoleDTO(role.id, role.value) for role in user.roles]

        user_info = UserDTO(user.id, user.username, roles)

        return user_info

    @rpc(String, _returns = UserDTO)
    def get_user_by_username(ctx, username):
        user = user_repository.get_user_by_username(username)

        if user is None:
            raise spyne.model.fault.Fault(faultcode='Client', faultstring='Specified user does not exist!')

        roles = [RoleDTO(role.id, role.value) for role in user.roles]
        user_info = UserDTO(user.id, user.username, roles)

        return user_info

    @rpc(String, _returns=String)
    def delete_user_by_username(ctx, username):
        if user_repository.get_user_by_username(username) is None:
            raise spyne.model.fault.Fault(faultcode='Client', faultstring='Specified user does not exist!')

        return user_repository.delete_user_by_username(username)

    @rpc(Integer, String, _returns=String)
    def update_username_for_user(ctx, id, updated_name):
        if user_repository.get_user_by_id(id) is None:
            raise spyne.model.fault.Fault(faultcode='Client', faultstring='Specified user does not exist!')

        if user_repository.update_username_for_user(id, updated_name) is True:
            return "Username was successfully updated."

        raise spyne.Fault(faultcode='Server', faultstring="Could not change the username for given user")

    @rpc(String, String, _returns=String)
    def update_password_for_user(ctx, username, updated_password):
        user = user_repository.get_user_by_username(username)

        if user is None:
            raise spyne.model.fault.Fault(faultcode='Client', faultstring='Specified user does not exist!')

        if user_repository.update_password_for_user(user, encode_password(updated_password)) is True:
            return "Password was successfully changed."

        raise spyne.Fault(faultcode='Server', faultstring="Could not change the password for given user")

    @rpc(String, String, _returns=String)
    def check_login(ctx, username, password):
        user = user_repository.get_user_by_username(username)

        if user == None:
            return "Incorrect login details!"

        if password_match(password, user.password):
            return "Successfully logged in!"

        return "Incorrect password!"

    @rpc(_returns=Iterable(UserDTO))
    def get_all_users(ctx):
        users = user_repository.get_users()
        if len(users) == 0:
            raise spyne.model.fault.Fault(faultcode='Client', faultstring='No user found!')

        users_dto = []
        for user in users:
            roles = [RoleDTO(role.id, role.value) for role in user.roles]
            users_dto.append(UserDTO(user.id, user.username, roles))

        return users_dto

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

###########################  ROLES  ###########################
    @rpc(_returns=Iterable(RoleDTO))
    def get_roles(ctx):
        roles = role_repository.get_roles()
        if len(roles) == 0:
            raise spyne.model.fault.Fault(faultcode='Client', faultstring='There are no entries in the roless table!')
        
        roles_info = []
        for role in roles:
            roles_info.append(RoleDTO(role.id, role.value))

        return roles_info
    @rpc(Integer, _returns=RoleDTO)
    def get_role_by_id(ctx, id):
        role = role_repository.get_role_by_id(id)
        if role is None:
            raise spyne.model.fault.Fault(faultcode='Client', faultstring='Specified role does not exist!')

        role_info = RoleDTO(role.id, role.value)

        return role_info

###########################  USERS_ROLES  ###########################

    @rpc(String, String, _returns=String)
    def add_role_for_user(ctx, user_username, role_name):
        user = user_repository.get_user_by_username(user_username)
        role = role_repository.get_role_by_name(role_name)

        if user is None:
            raise spyne.model.fault.Fault(faultcode='Client', faultstring='Specified user does not exist!')

        if role is None:
            raise spyne.model.fault.Fault(faultcode='Client', faultstring='Specified role does not exist!')


        session = Session.object_session(user)
        if session is None:
            session = Session()

        user = session.merge(user)
        role = session.merge(role)

        if role in user.roles:
            return "Role already assigned to the user."

        if users_roles_repository.add_role_for_user(user, role, session) is True:
            return "Role was successfully added to the user."

        raise spyne.Fault(faultcode='Server', faultstring="Could not update roles for given user")

    @rpc(Integer, _returns=Iterable(String))
    def get_user_roles(ctx, user_id):
        roles = list(users_roles_repository.get_user_roles(user_id))
        return roles