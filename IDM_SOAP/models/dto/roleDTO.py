from spyne import Integer, String, ComplexModel
class RoleDTO(ComplexModel):
    role_id = Integer()
    role_name = String()

    def __init__(self, id, rolename):
        super(RoleDTO, self).__init__()
        self.role_id = id
        self.role_name = rolename