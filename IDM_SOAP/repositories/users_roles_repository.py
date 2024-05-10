import spyne

from models.user_orm import User
from base.sql_base import Session

def add_role_for_user(user, role, session):
    if session is None:
        raise spyne.model.fault.Fault(faultcode='Client', faultstring='Session object is required.')

    user.roles.append(role)
    try:
        session.add(user)
        session.commit()
    except Exception as exc:
        print(f"Failed to add role to the user - {exc}")

    return True

def get_user_roles(user_id):
    session = Session()
    user = session.query(User).get(user_id)
    if user is None:
        raise spyne.model.fault.Fault(faultcode='Client', faultstring='User does not exist')

    roles = []
    for r in user.roles:
        roles.append(r.value)

    if len(roles) == 0:
        raise spyne.model.fault.Fault(faultcode='Client', faultstring='Current user has no associated roles')

    return roles
